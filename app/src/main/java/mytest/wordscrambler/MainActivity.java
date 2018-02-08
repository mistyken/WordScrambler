package mytest.wordscrambler;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.CallScreeningService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText textMessage;
    private EditText phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMessage = (EditText) findViewById(R.id.textMessage);
    }

    public void scrambleMessage(View view){
        String holder = textMessage.getText().toString();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        if(holder != null && holder.length() > 0){
            String[] phrase = holder.split(" ");
            for(String word: phrase){
                char[] wordChar = word.toCharArray();
                for(int i = 1; i < wordChar.length - 2; i++){
                    int index = random.nextInt(i + 1) + 1;
                    char temp = wordChar[index];
                    wordChar[index] = wordChar[i];
                    wordChar[i] = temp;
                }
                sb.append(String.valueOf(wordChar)).append(" ");
            }
        }

        holder = sb.toString().toLowerCase();
        
        textMessage.setText(holder);
        Toast toast = Toast.makeText(getApplicationContext(), "Text Scrambled", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void copyMessage(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String holder = textMessage.getText().toString();
        ClipData clip = ClipData.newPlainText("textMessage", holder);
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void clearMessage(View view){
        textMessage.setText("");
        Toast toast = Toast.makeText(getApplicationContext(), "Text Cleared", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void sendMessage(View view) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptView = li.inflate(R.layout.ph_num_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        phoneNum = (EditText) promptView.findViewById(R.id.toPhoneNumber);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String number = phoneNum.getText().toString();
                        number = number.replaceAll("\\D+", "");

                        if(number.length() != 11){
                            Toast toast = Toast.makeText(getApplicationContext(), "Phone number too short/too long", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            try {
                                post(getApplicationContext().getString(R.string.test_end_point).concat("/sms"), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if(response.isSuccessful()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Message had been sent to " + phoneNum.getText().toString(), Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    phoneNum.setText("");
                                                }
                                            });
                                        } else {
                                            final int errorCode = response.code();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "UNSUCCESSFUL! Message was NOT sent to "
                                                            + phoneNum.getText().toString()
                                                            + " Error Code:" + errorCode, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    phoneNum.setText("");
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    Call post(String url, Callback callback) throws IOException {
        OkHttpClient mClient = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("To", phoneNum.getText().toString())
                .add("Body", textMessage.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call response = mClient.newCall(request);
        response.enqueue(callback);
        return response;
    }
}
