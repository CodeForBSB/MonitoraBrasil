package com.gamfig.monitorabrasil.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.gamfig.monitorabrasil.classes.twitter.MyTwitterApiClient;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRedeActivity extends Activity{
    private TwitterLoginButton btnTwitter;
    private static final String KEY = "reauth";
    private Button btnCompartilharFace;

    //face
    private UiLifecycleHelper uiLifecycleHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            onSessionStateChange(session,sessionState,e);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_rede);

        //twitter
        btnTwitter = (TwitterLoginButton) findViewById(R.id.login_button);
        TwitterSession sessionTwitter = Twitter.getSessionManager().getActiveSession();
       // sessionTwitter = null;
        if(sessionTwitter!=null){
            carregaImagem(false);

        }

        btnTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                Usuario user = new Usuario();
                String idTwitter = String.valueOf(result.data.getUserId());
                user.setIdTwitter(idTwitter);
                user.setNome(result.data.getUserName());
                SharedPreferences.Editor editor = AppController.getInstance().getSharedPref().edit();
                editor.putString("idtwitter",idTwitter);
                editor.commit();

                fillForm(user);
                carregaImagem(true);

//                salvarUser();
            }


            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(),"Não foi possíve conectar ao Twitter",
                        Toast.LENGTH_SHORT).show();
                Crashlytics.logException(exception);
            }
        });

        //facebook
        uiLifecycleHelper = new UiLifecycleHelper(this,callback);
        uiLifecycleHelper.onCreate(savedInstanceState);

        //Facebook
        LoginButton btnFace = (LoginButton)findViewById(R.id.btnFace);
        btnFace.setPublishPermissions(Arrays.asList("email","public_profile","user_friends"));

        if(savedInstanceState != null){
            reauth = savedInstanceState.getBoolean(KEY);
        }

        //botao compartilhar
        btnCompartilharFace = (Button)findViewById(R.id.btnShare);

        btnCompartilharFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContent();
            }
        });

        btnCompartilharFace.setVisibility(View.GONE);
        Session session = Session.getActiveSession();
        if(session != null &&  session.isOpened()){
            btnCompartilharFace.setVisibility(View.VISIBLE);
        }

        Usuario user = new UserDAO(getApplicationContext()).getUserCompleto();
        if (user != null) {
            fillForm(user);
        }

    }

    public void carregaImagem(final boolean salva){
        btnTwitter.setVisibility(View.GONE);
        final long idTwitter = Long.valueOf(AppController.getInstance().getSharedPref().getString("idtwitter", null));
        MyTwitterApiClient client = new MyTwitterApiClient(Twitter.getSessionManager().getActiveSession());
        client.getListService2().show(idTwitter,
                new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        ImageView img = (ImageView) findViewById(R.id.fotoTwitter);
                        AppController.getInstance().getmImagemLoader().displayImage(result.data.profileImageUrl, img);
                        img.setVisibility(View.VISIBLE);
                        if(salva){
                            Usuario user = new Usuario();
                            user.setIdTwitter(String.valueOf(idTwitter));
                            user.setUrlFoto(result.data.profileImageUrl);
                            atualizarUsuario(user);
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("twittercommunity", "exception is " + exception);
                    }
                });
    }

    private void atualizarUsuario(final Usuario user) {
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST , AppController.URL + "rest/user_atualiza_twitter.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                Toast.makeText(getApplicationContext(),"Informações foram salvas.",Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("idtwitter",user.getIdTwitter());
                params.put("urlfoto",user.getUrlFoto());
                params.put("id", String.valueOf(AppController.getInstance().getSharedPref().getInt(getString(R.string.id_key_idcadastro_novo),0)));
                return params;
            }};
        AppController.getInstance().addToRequestQueue(request,"tag");
    }

    public void actionSalvar(View view) {
        salvarUser();

    }

    public void salvarUser() {

        Usuario user = new Usuario();
        user.setId(new UserDAO(getApplicationContext()).getIdUser());
        // nome
        TextView txtNome = (TextView) findViewById(R.id.txtNome);
        user.setNome(txtNome.getText().toString());
        // email
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        user.setEmail(txtEmail.getText().toString());
        // uf
        Spinner uf = (Spinner) findViewById(R.id.uf);
        String ufSelecionada = (String) uf.getItemAtPosition(uf.getSelectedItemPosition());
        user.setUf(ufSelecionada);

        // faixaetaria
        Spinner faixaEtaria = (Spinner) findViewById(R.id.faixaEtaria);
        String faixaSelecionada = (String) faixaEtaria.getItemAtPosition(faixaEtaria.getSelectedItemPosition());
        user.setFaixaEtaria(faixaSelecionada);

        // sexo
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
        int idSelecionado = rg.getCheckedRadioButtonId();
        switch (idSelecionado) {
            case R.id.radioFem:
                user.setSexo("f");
                break;
            case R.id.radioMasc:
                user.setSexo("m");
                break;
            case R.id.radioOutro:
                user.setSexo("o");
                break;

            default:
                break;
        }
        // receber notificacao
        Switch sw = (Switch) findViewById(R.id.switch1);
        user.setReceberNotificacao(String.valueOf(sw.isChecked()));

        //facebook
        String idfacebook = AppController.getInstance().getSharedPref().getString("idfacebook",null);
        if(idfacebook!=null){
            user.setIdFacebook(idfacebook);
        }

        new SalvaUser(user).execute();
        Toast.makeText(LoginRedeActivity.this, "Informações salvas com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public class SalvaUser extends AsyncTask<Void, Void, Void> {
        Usuario user;

        public SalvaUser(Usuario user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            new UserDAO(getApplicationContext()).atualizaUsuario(user);

            return null;
        }

        protected void onPostExecute() {

        }
    }

    private void fillForm(Usuario user) {


        // nome
        TextView txtNome = (TextView) findViewById(R.id.txtNome);
        txtNome.setText(user.getNome());

        // email
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtEmail.setText(user.getEmail());

        // uf
        Spinner uf = (Spinner) findViewById(R.id.uf);
        String[] ufs = getResources().getStringArray(R.array.ufs);
        int i = 0;
        for (String ufItem : ufs) {
            if (ufItem.equals(user.getUf())) {
                uf.setSelection(i);
            }
            i++;
        }

        // faixa etaria
        Spinner faixaEtaria = (Spinner) findViewById(R.id.faixaEtaria);
        String[] faixasEtaria = getResources().getStringArray(R.array.faixa);
        i = 0;
        for (String faixaItem : faixasEtaria) {
            if (faixaItem.equals(user.getFaixaEtaria())) {
                faixaEtaria.setSelection(i);
            }
            i++;
        }

        // sexo
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
        if (user.getSexo() != null) {
            if (user.getSexo().equals("f")) {
                rg.check(R.id.radioFem);
            } else {
                if (user.getSexo().equals("m")) {
                    rg.check(R.id.radioMasc);
                } else {
                    rg.check(R.id.radioOutro);
                }
            }
        }


        // receber notificacao
        Switch sw = (Switch) findViewById(R.id.switch1);
        if (user.getReceberNotificacao() == null) {
            user.setReceberNotificacao("true");
        }
        sw.setChecked(Boolean.valueOf(user.getReceberNotificacao()));

    }

    @Override
    protected void onResume(){
        super.onResume();
        Session session = Session.getActiveSession();
        if(session != null && (session.isClosed() || session.isOpened())){
            onSessionStateChange(session,session.getState(),null);
        }
        uiLifecycleHelper.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(KEY,reauth);
        uiLifecycleHelper.onSaveInstanceState(bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);


        btnTwitter.onActivityResult(requestCode, resultCode, data);

    }


    //METODOS FACEBOOK
    public void onSessionStateChange(final Session session, SessionState sessionState, Exception e){
        if(session != null && session.isOpened()){
            if(reauth && (sessionState.equals(SessionState.OPENED_TOKEN_UPDATED))){
                reauth = false;
                shareContent();
            }

            Log.i("monitora","conectado");
            Request.newMeRequest(session,new Request.GraphUserCallback(){

                @Override
                public void onCompleted(GraphUser graphUser, Response response) {
                    if(graphUser!=null){
                        Log.i("monitora",graphUser.getFirstName());
                        ProfilePictureView foto = (ProfilePictureView)findViewById(R.id.fotoFace);
                        foto.setProfileId(graphUser.getId());
                        foto.setVisibility(View.VISIBLE);

                        //verificar se o idfacebook esta salvo
                        String idfacebook = AppController.getInstance().getSharedPref().getString("idfacebook",null);
                        if(null == idfacebook){
                            SharedPreferences.Editor editor = AppController.getInstance().getSharedPref().edit();
                            editor.putString("idfacebook",graphUser.getId());
                            editor.commit();
                            //verificar se tem algum usuario para com esse id
                            verificaCadastroFacebook(graphUser);
                        }
                    }
                }
            }).executeAsync();
        }
        else{
            if(session != null && session.isClosed()){
             //logout
                SharedPreferences.Editor editor = AppController.getInstance().getSharedPref().edit();
                editor.putString("idfacebook",null);
                editor.commit();
            }
        }
    }

    public void verificaCadastroFacebook(final GraphUser graphUser){
        Usuario user = new Usuario();
        user.setEmail(graphUser.getProperty("email").toString());
        user.setNome(graphUser.getFirstName());
        fillForm(user);

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST , AppController.URL + "rest/getidfacebook.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                //se tiver atualizar o userid
                                SharedPreferences.Editor editor = AppController.getInstance().getSharedPref().edit();
                                editor.putInt(getString(R.string.id_key_idcadastro_novo), jsonObject.getInt("id"));
                                Usuario user = new Usuario();
                                user.setId(jsonObject.getInt("id"));
                                user.setNome(jsonObject.getString("nome"));
                                Gson gson = new Gson();
                                editor.putString(getString(R.string.id_key_user), gson.toJson(user));
                                editor.commit();
                            }
                            salvarUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("idfacebook",graphUser.getId());
                params.put("idUser",String.valueOf(AppController.getInstance().getSharedPref().getInt(getString(R.string.id_key_idcadastro_novo),0)));
                return params;
            }};
        AppController.getInstance().addToRequestQueue(request,"tag");
    }

    private boolean reauth = false;
    public void shareContent(){
        Session session = Session.getActiveSession();

        if(session != null){
            List<String> permissions = session.getPermissions();
            List<String> newPermissions = Arrays.asList("publish_actions");

            if(!verifyPermissions(permissions,newPermissions)){
                reauth = true;
                Session.NewPermissionsRequest npr = new Session.NewPermissionsRequest(this,newPermissions);
                session.requestNewPublishPermissions(npr);
                return;
            }
            Bundle params = new Bundle();
            params.putString("name","Monitora, Brasil! App para monitorar os parlamentares");
            params.putString("caption","App para monitorar os parlamentares");
            params.putString("description","O aplicativo Monitora, Brasil! é uma ferramenta que possibilita a qualquer pessoa pesquisar e monitorar o que os Deputados Federais e Senadores estão fazendo na Câmara dos Deputados e no Senado.\n" +
                    "É possível verificar a assiduidade, os projetos propostos, rankings, Twitter e outras informações.\n" +
                    "Os dados são extraídos do site da Câmara dos Deputados, Senado Federal, TSE e Transparência Brasil.\n" +
                    "Exerça sua cidadania, monitore, dialogue com os Parlamentares e contribua para uma atividade legislativa mais transparente e eficiente.\n" +
                    "Agora é possível monitorar os projetos, basta selecionar qual projeto deseja acompanhar que caso ocorra uma movimentação você receberá uma notificação.\n" +
                    "\n" +
                    "Site: http://www.monitorabrasil.com");
            params.putString("link","https://play.google.com/store/apps/details?id=com.gamfig.monitorabrasil");
            params.putString("picture","http://www.monitorabrasil.com/images/MonitoraBrasil.png");

            Request.Callback callback1 = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if(response.getError()==null){
                        Toast.makeText(LoginRedeActivity.this,"Mensagem enviada",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginRedeActivity.this,"Erro",Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Request request = new Request(session,"me/feed",params, HttpMethod.POST,callback1);
            request.executeAsync();


        }

    }

    public boolean verifyPermissions(List<String> permissions, List<String> newPermissions){
        for(String p: permissions){
            if(newPermissions.contains(p)){
                return true;
            }
        }
        return false;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_rede, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
