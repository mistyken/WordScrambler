package mytest.wordscrambler;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scrambleMessage(View view){
        EditText editText = (EditText) findViewById(R.id.textMessage);
        String textMessage = editText.getText().toString();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        if(textMessage != null && textMessage.length() > 0){
            String[] phrase = textMessage.split(" ");
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

        textMessage = sb.toString().toLowerCase();
        
        editText.setText(textMessage);
        Toast toast = Toast.makeText(getApplicationContext(), "Text Scrambled", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void copyMessage(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        EditText editText = (EditText) findViewById(R.id.textMessage);
        String textMessage = editText.getText().toString();
        ClipData clip = ClipData.newPlainText("textMessage", textMessage);
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void clearMessage(View view){
        EditText editText = (EditText) findViewById(R.id.textMessage);
        editText.setText("");
        Toast toast = Toast.makeText(getApplicationContext(), "Text Cleared", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void sendMessage(View view) {

    }
}
