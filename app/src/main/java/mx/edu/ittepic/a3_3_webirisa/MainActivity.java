package mx.edu.ittepic.a3_3_webirisa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextAlumnoId, editTextNombre, editTextDireccion;
    ProgressBar progressBar;
    ListView listView;
    Button buttonAddUpdate;

    List<Alumno> AlList;

    boolean isUpdating = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAlumnoId = (EditText) findViewById(R.id.editTextAlumnoId);
        editTextNombre = (EditText) findViewById(R.id.editTextNombre);
        editTextDireccion = (EditText) findViewById(R.id.editTextDireccion);

        buttonAddUpdate = (Button) findViewById(R.id.buttonAddUpdate);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listViewAlumnos);

        AlList = new ArrayList<>();



        buttonAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUpdating) {
                    updateHero();
                } else {
                    createHero();
                }
            }
        });
        readHeroes();
    }

    private void createHero() {
        String nombre = editTextNombre.getText().toString().trim();
        String direccion = editTextDireccion.getText().toString().trim();


        if (TextUtils.isEmpty(nombre)) {
            editTextNombre.setError("Ingrese nombre");
            editTextNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(direccion)) {
            editTextDireccion.setError("Ingrese direccion");
            editTextDireccion.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("nombre", nombre);
        params.put("direccion", direccion);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_HERO, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void readHeroes() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_HEROES, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updateHero() {
        String id = editTextAlumnoId.getText().toString();
        String nombre = editTextNombre.getText().toString().trim();
        String direccion = editTextDireccion.getText().toString().trim();


        if (TextUtils.isEmpty(nombre)) {
            editTextNombre.setError("Ingrese nombre");
            editTextNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(direccion)) {
            editTextDireccion.setError("Ingrese direccion");
            editTextDireccion.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("idalumno", id);
        params.put("nombre", nombre);
        params.put("direccion", direccion);


        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_HERO, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUpdate.setText("Agregar");

        editTextNombre.setText("");
        editTextDireccion.setText("");

        isUpdating = false;
    }

    private void deleteHero(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_HERO + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshHeroList(JSONArray alumnos) throws JSONException {
        AlList.clear();

        for (int i = 0; i < alumnos.length(); i++) {
            JSONObject obj = alumnos.getJSONObject(i);

            AlList.add(new Alumno(
                    obj.getInt("idalumno"),
                    obj.getString("nombre"),
                    obj.getString("direccion")
            ));
        }

        HeroAdapter adapter = new HeroAdapter(AlList);
        listView.setAdapter(adapter);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshHeroList(object.getJSONArray("alumnos"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    class HeroAdapter extends ArrayAdapter<Alumno> {
        List<Alumno> alumnoClassList;

        public HeroAdapter(List<Alumno> alumnoClassList) {
            super(MainActivity.this, R.layout.layout_hero_list, alumnoClassList);
            this.alumnoClassList = alumnoClassList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_hero_list, null, true);

            TextView textViewNombre = listViewItem.findViewById(R.id.textViewNombre);

            TextView textViewUpdate = listViewItem.findViewById(R.id.textViewUpdate);
            TextView textViewDelete = listViewItem.findViewById(R.id.textViewDelete);

            final Alumno alumnoClass = alumnoClassList.get(position);

            textViewNombre.setText(alumnoClass.getNombre());

            textViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdating = true;
                    editTextAlumnoId.setText(String.valueOf(alumnoClass.getId()));
                    editTextNombre.setText(alumnoClass.getNombre());
                    editTextDireccion.setText(alumnoClass.getDireccion());
                    buttonAddUpdate.setText("Actualizar");
                }
            });

            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Borrar " + alumnoClass.getNombre())
                            .setMessage("Esta seguro de borrar este registro?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteHero(alumnoClass.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });

            return listViewItem;
        }

    }
}
