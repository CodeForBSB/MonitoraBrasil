package com.gamfig.monitorabrasil.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gamfig.monitorabrasil.DAO.DataBaseHelper;
import com.gamfig.monitorabrasil.DAO.PoliticoDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.PoliticoAdapter;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.FloatingActionButton;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Presenca;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComparatorActivity extends ActionBarActivity implements AdapterView.OnItemClickListener  {

    private List<Politico> listaPoliticos = new ArrayList<Politico>();
    private List<Politico> listaPoliticosFiltrado = new ArrayList<Politico>();
    private DataBaseHelper dbh;
    private PoliticoDAO politicoDAO;
    private String nomePolitico1;
    private String nomePolitico2;
    private LinearLayout llComparator;
    FloatingActionButton btnCompartilhar;


    PoliticoAdapter adapterPoliticos = null;
    AlertDialog myalertDialog;
    int textlength=0;
    int escolhaDeputado=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparator);
        llComparator = (LinearLayout)findViewById(R.id.llComparator);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String data = dateFormat.format(date);
        TextView txtData = (TextView)findViewById(R.id.txtData);
        txtData.setText("Dados extraídos em "+data);

        try {
            dbh = new DataBaseHelper(this);
            politicoDAO = new PoliticoDAO(dbh.getConnectionSource());
            Map<String,Object> values = new HashMap<String,Object>();
            values.put("tipo",'c');
            listaPoliticos = politicoDAO.queryForFieldValues(values);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button btnDeputado1 = ( Button) findViewById(R.id.btnDeputado1);
        btnDeputado1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abreEscolha();
                escolhaDeputado =1;
            }
        });

        Button btnDeputado2 = (Button)findViewById(R.id.btnPolitico2);
        btnDeputado2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abreEscolha();
                escolhaDeputado=2;
            }
        });

        btnCompartilhar = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_share_white))
                .withButtonColor(getResources().getColor(R.color.fruit_laranja2))
                .withGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                .withMargins(0, 100, 0, 0)
                .create();

        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviaImagem();
            }
        });
        btnCompartilhar.setVisibility(View.INVISIBLE);
    }

    public void abreEscolha(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(ComparatorActivity.this);

        final EditText editText = new EditText(ComparatorActivity.this);
        final ListView listview=new ListView(ComparatorActivity.this);
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
        editText.setHint("Pode filtrar por uf Ex: UF SP");
        LinearLayout layout = new LinearLayout(ComparatorActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);
        adapterPoliticos=new PoliticoAdapter(ComparatorActivity.this,R.layout.listview_item_politico, listaPoliticos);
        listview.setAdapter(adapterPoliticos);
        listview.setOnItemClickListener(ComparatorActivity.this);
        editText.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s){

            }
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after){

            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String texto = editText.getText().toString().toLowerCase();
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                textlength = editText.getText().length();
                listaPoliticosFiltrado.clear();
                boolean procura = false;

                //busca por uf
                if(texto.length() >=5){
                    if(texto.substring(0,3).toLowerCase().equals("uf ")){
                        String uf = texto.substring(3,5);
                        for (int i = 0; i < listaPoliticos.size(); i++)
                        {
                            if(listaPoliticos.get(i).getUf().toLowerCase().contains(uf))
                                {
                                    listaPoliticosFiltrado.add(listaPoliticos.get(i));
                                }
                        }
                        if(texto.length() > 5){
                            procura = true;
                            texto = texto.substring(5,(texto.length())).trim();

                        }
                    }  else{
                        procura=true;
                    }
                }else{
                    procura = true;
                }

                if(procura)
                    for (int i = 0; i < listaPoliticos.size(); i++)
                    {
                        if (textlength <= listaPoliticos.get(i).getNome().length())
                        {

                            if(listaPoliticos.get(i).getNome().toLowerCase().contains(texto.trim()))
                            {
                                listaPoliticosFiltrado.add(listaPoliticos.get(i));
                            }
                        }
                    }
                listview.setAdapter(new PoliticoAdapter(ComparatorActivity.this,R.layout.listview_item_politico, listaPoliticosFiltrado));
            }
        });
        myDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myalertDialog=myDialog.show();
    }

    /*
    Busca informacao do politico
     */
    @Override
    public void onItemClick(AdapterView arg0, View arg1, int position, long arg3) {

        myalertDialog.dismiss();
        TextView txtNome;
        TextView txtPartido;
        ImageView foto;
        final ProgressBar pb;
        llComparator.setVisibility(View.VISIBLE);

        Politico politico;
        if(listaPoliticosFiltrado.size() > 0)
            politico = listaPoliticosFiltrado.get(position);
        else
            politico = listaPoliticos.get(position);

        listaPoliticosFiltrado.clear();
        if(escolhaDeputado==1){
            txtNome = (TextView) findViewById(R.id.txtDeputado1);
            txtPartido = (TextView) findViewById(R.id.txtPartido1);
            foto = (ImageView)findViewById(R.id.imgPolitico1);
            pb = (ProgressBar)findViewById(R.id.pb1);
            nomePolitico1 = politico.getNome()+" "+politico.getTwitter();
        }else{
            txtNome = (TextView) findViewById(R.id.txtPolitico2);
            txtPartido = (TextView) findViewById(R.id.txtPartido2);
            foto = (ImageView)findViewById(R.id.imgPolitico2);
            pb = (ProgressBar)findViewById(R.id.pb2);
            nomePolitico2 = politico.getNome()+" "+politico.getTwitter();
        }
        pb.setVisibility(View.VISIBLE);
        resetForm();


        txtNome.setText(politico.getNome());
        txtPartido.setText(politico.getSiglaPartido()+"-"+politico.getUf());


        Imagens.getFotoPolitico(politico,foto,true);
        setProgressBarIndeterminateVisibility(true);
        //busca dados
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST , AppController.URL +
                "rest/politico_getatividade.php?id=" + String.valueOf(politico.getIdCadastro())+"&dep="+String.valueOf(escolhaDeputado),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Gson gson = new Gson();
                            Politico politico = gson.fromJson(jsonObject.getString("dados"), Politico.class);
                            fillForm(politico,jsonObject.getInt("dep"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pb.setVisibility(View.GONE);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                return params;
            }};
        AppController.getInstance().addToRequestQueue(request,"buscaDeputado"+String.valueOf(escolhaDeputado));
    }

    /**
     * Limpa tela
     */
    private void resetForm() {
        TextView txtPresenca;
        TextView txtGasto;
        TextView txtProjetos;
        TextView txtProjetosAprovados;
        TextView txtTwitter;
        TextView txtVotos;
        if(escolhaDeputado == 1){
            txtPresenca = (TextView)findViewById(R.id.txtPresenca1);
            txtGasto= (TextView)findViewById(R.id.txtGastos1);
            txtProjetos = (TextView)findViewById(R.id.txtProjetos1);
            txtProjetosAprovados = (TextView)findViewById(R.id.txtProjetosAprovado1);
            txtTwitter = (TextView)findViewById(R.id.txtTwitter1);
            txtVotos = (TextView)findViewById(R.id.txtVotos1);

        }else{

            txtPresenca = (TextView)findViewById(R.id.txtFalta2);
            txtGasto= (TextView)findViewById(R.id.txtGastos2);
            txtProjetos = (TextView)findViewById(R.id.txtProjetos2);
            txtProjetosAprovados = (TextView)findViewById(R.id.txtProjetosAprovado2);
            txtTwitter = (TextView)findViewById(R.id.txtTwitter2);
            txtVotos = (TextView)findViewById(R.id.txtVotos2);
        }

        //falta
        txtPresenca.setText("-");

        //gastos
        txtGasto.setText("-");

        //projetos
        txtProjetos.setText("-");
        txtProjetosAprovados.setText("-");
        txtTwitter.setText("-");
        txtVotos.setText("-");
    }

    /**
     * Preenche tela com informacoes do politico
     * @param politico
     */
    private void fillForm(Politico politico, int deputado) {

        TextView txtPresenca;
        TextView txtGasto;
        TextView txtProjetos;
        TextView txtNumSeguidores;
        TextView txtNumProjetosAprovados;
        TextView txtNumVotos;
        if(deputado == 1){
            txtPresenca = (TextView)findViewById(R.id.txtPresenca1);
            txtGasto= (TextView)findViewById(R.id.txtGastos1);
            txtProjetos = (TextView)findViewById(R.id.txtProjetos1);
            txtNumSeguidores = (TextView)findViewById(R.id.txtTwitter1);
            txtNumProjetosAprovados = (TextView)findViewById(R.id.txtProjetosAprovado1);
            txtNumVotos = (TextView)findViewById(R.id.txtVotos1);

        }else{

            txtPresenca = (TextView)findViewById(R.id.txtFalta2);
            txtGasto= (TextView)findViewById(R.id.txtGastos2);
            txtProjetos = (TextView)findViewById(R.id.txtProjetos2);
            txtNumSeguidores = (TextView)findViewById(R.id.txtTwitter2);
            txtNumProjetosAprovados = (TextView)findViewById(R.id.txtProjetosAprovado2);
            txtNumVotos = (TextView)findViewById(R.id.txtVotos2);
        }

        //falta
        int totalFalta=0;
        for (Presenca presenca : politico.getPresenca()) {
            totalFalta += presenca.getNrAusenciaJustificada() ;
        }

        txtPresenca.setText(String.valueOf(totalFalta));
        //gastos
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        DecimalFormat df2 = new DecimalFormat("#,###,##0");
        txtGasto.setText(df.format(politico.getValor()));
        //projetos
        txtProjetos.setText(String.valueOf(politico.getNrProjetos()));
        setProgressBarIndeterminateVisibility(false);
        //twitter
        txtNumSeguidores.setText(df2.format(politico.getNrSeguidoresTwitter()));
        //numProjetosAprovados
        txtNumProjetosAprovados.setText(String.valueOf(politico.getNrProjetosAprovados()));
        //numVotos
        txtNumVotos.setText(df2.format(politico.getNrVotos()));

        //verifica se pode mostrar o botao para compartilhar
        TextView txtGasto1= (TextView)findViewById(R.id.txtDeputado1);
        TextView txtGasto2= (TextView)findViewById(R.id.txtPolitico2);
        if(!txtGasto1.getText().equals("-") && !txtGasto2.getText().equals("-")){
            btnCompartilhar.setVisibility(View.VISIBLE);
        }


    }


    /**
     * Compartilha a imagem gerada
     */
    private void enviaImagem() {
           /* final Date date = new Date();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
            StringRequest request = new StringRequest(com.android.volley.Request.Method.POST , AppController.URL + "rest/upload_image.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            public Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("image",encodeImagetoString());

                params.put("filename",dateFormat.format(date) + ".png");
                return params;
            }};*/
        // AppController.getInstance().addToRequestQueue(request,"tag");


        Bitmap inImage = screenShot(findViewById(R.id.llComparator));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.title_activity_comparator));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, nomePolitico1+" X "+nomePolitico2+" #MonitoraBrasil #Câmara http://www.monitorabrasil.com");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Escolha o app para compartilhar"));
    }

    public String encodeImagetoString() {
        Bitmap bitmap =  screenShot(findViewById(R.id.llComparator));
        //bitmap = getResizedBitmap(bitmap,450 	,	530);
        BitmapFactory.Options options = null;
        options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedString = Base64.encodeToString(byte_arr, 0);
        return encodedString;

    }



    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }




    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comparator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
