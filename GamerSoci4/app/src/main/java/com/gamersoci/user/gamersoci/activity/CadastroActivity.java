package com.gamersoci.user.gamersoci.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gamersoci.user.gamersoci.R;
import com.gamersoci.user.gamersoci.helper.ConfiguracaoFirebase;
import com.gamersoci.user.gamersoci.model.Usuario;
import com.gamersoci.user.gamersoci.model.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private static String CHANNEL_ID = "simplified_coding";
    private static String CHANNEL_Name = "simplified coding";
    private static String CHANNEL_Descr = "simplified coding notification";

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    //objeto atutenticação firebase
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_Name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_Descr);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        inicializarComponentes();

        /**Cadastrar usuario*/
        progressBar.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome  = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textosenha = campoSenha.getText().toString();

                if( !textoNome.isEmpty() ){
                    if( !textoEmail.isEmpty() ){
                        if( !textosenha.isEmpty() ){

                            usuario = new Usuario();
                            usuario.setNome( textoNome );
                            usuario.setEmail( textoEmail );
                            usuario.setSenha( textosenha );
                            cadastrar( usuario );

                        }else{
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o email!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    /**
     * Método responsável por cadastrar usuário com e-mail e senha
     * e fazer validações ao fazer o cadastro
     */
    public void cadastrar(final Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ){

                            try {

                                progressBar.setVisibility(View.GONE);

                                //Salvar dados no firebase
                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId( idUsuario );
                                usuario.salvar();

                                //Salvar dados no profile do Firebase
                               UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );

                                Toast.makeText(CadastroActivity.this,
                                        "Cadastro com sucesso",
                                        Toast.LENGTH_SHORT).show();
                                displayNotification();
                                startActivity( new Intent(getApplicationContext(), MainActivity.class));
                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }



                        }else {

                            progressBar.setVisibility( View.GONE );

                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Por favor, digite um e-mail válido";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Este conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this,
                                    "Erro: " + erroExcecao ,
                                    Toast.LENGTH_SHORT).show();


                        }

                    }
                }
        );

    }

    public void inicializarComponentes(){

        campoNome       = findViewById(R.id.editCadastroNome);
        campoEmail      = findViewById(R.id.editCadastroEmail);
        campoSenha      = findViewById(R.id.editCadastroSenha);
        botaoCadastrar  = findViewById(R.id.buttonEntrar);
        progressBar     = findViewById(R.id.progressCadastro);

        campoNome.requestFocus();

    }

    public void displayNotification(){
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                .setContentTitle("Bem Vindo ao GamerSoci")
                .setContentText("Estamos felizes com a sua presença =D")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(this);

        mNotificationMgr.notify(1,mbuilder.build());
    }
}



