package rowan.rsasecuremessaging;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Hide the Title Bar
       //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         //       WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

// Hide the Status Bar

    }






    public void generateClick(View view) {
        int p = randomPrime(100, 200);
        int q = randomPrime(100, 200);

        TextView publicKeyOutput = (TextView) findViewById(R.id.publicKeyOutput);
        String publicKeyE = generatePublicKey(p, q).get(0).toString();
        String publicKeyN = generatePublicKey(p, q).get(1).toString();
        String publicKeyOutputString = publicKeyE + " " + publicKeyN;
        publicKeyOutput.setText(publicKeyOutputString);

        TextView privateKeyOutput = (TextView) findViewById(R.id.privateKeyOutput);
        String privateKeyD = generatePrivateKey(p, q).get(0).toString();
        String privateKeyN = generatePrivateKey(p, q).get(1).toString();
        String priavteKeyOutputString = privateKeyD + " " + privateKeyN;
        privateKeyOutput.setText(priavteKeyOutputString);
    }


//**************************************************************************************************




    public static int randomPrime(int greaterThan, int lessThan) {
        int num = 0;
        Random rand = new Random(); // generate a random number
        num = rand.nextInt(1000) + 1;

        while (!isPrime(num) || num>lessThan || num<greaterThan) {
            num = rand.nextInt(1000) + 1;
        }

        return num; // print the number
    }

    private static boolean isPrime(int inputNum) {
        if (inputNum <= 3 || inputNum % 2 == 0)
            return inputNum == 2 || inputNum == 3; // this returns false if
        // number is <=1 & true if
        // number = 2 or 3
        int divisor = 3;
        while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0))
            divisor += 2; // iterates through all possible divisors
        return inputNum % divisor != 0; // returns true/false
    }

    public ArrayList<BigInteger> generatePublicKey(int p, int q) {

        ArrayList<BigInteger> publickey = new ArrayList<BigInteger>();
        publickey.add(generateE(p, q));
        publickey.add(generateN(p, q));

        return publickey;
    }

    public static ArrayList<BigInteger> generatePrivateKey(int p, int q) {
        ArrayList<BigInteger> privatekey = new ArrayList<BigInteger>();
        privatekey.add(generateD(p, q));
        privatekey.add(generateN(p, q));

        return privatekey;
    }

    public static BigInteger generateN(int p, int q) {
        return BigInteger.valueOf(p * q);
    }

    public static BigInteger generatePhiD(int p, int q) {
        BigInteger z = BigInteger.valueOf((p - 1) * (q - 1));
        return z;
    }

    public static BigInteger generateE(int p, int q) {
        BigInteger e = BigInteger.valueOf((int) ((Math.log(p) * Math.log(q)) / Math.log(2)));
        BigInteger gcd = e.gcd(generatePhiD(p, q));
        while (gcd.intValue() != 1) {
            e = e.add(BigInteger.valueOf(1));
            gcd = e.gcd(generatePhiD(p, q));
        }

        return e;

    }

    public static BigInteger generateD(int p, int q) {
        BigInteger e = generateE(p, q);
        BigInteger d = e.add(BigInteger.valueOf(1));
        BigInteger z = generatePhiD(p, q);

        while (! ((((e.multiply(d))).mod(z)).compareTo(BigInteger.valueOf(1)) == 0)) {
            d = d.add(BigInteger.valueOf(1));
        }
        return d;
    }






//**************************************************************************************************





    public void encryptClick(View view) {
        TextView encryptedStringInput = (TextView) findViewById(R.id.encryptedStringInput);
        TextView encryptedStringOutput = (TextView) findViewById(R.id.encryptedStringOutput);
        TextView publicKeyE = (TextView) findViewById(R.id.publicKeyEInput);
        TextView publicKeyN = (TextView) findViewById(R.id.publicKeyNInput);

        String stringEncryptedStringInput = encryptedStringInput.getText().toString();
        String stringpublicKeyE = publicKeyE.getText().toString();
        String stringpublicKeyN = publicKeyN.getText().toString();

        BigInteger bigPublicKeyE = new BigInteger(stringpublicKeyE);
        BigInteger bigPublicKeyN = new BigInteger(stringpublicKeyN);
        ArrayList<BigInteger> publicKey = getPublicKey(bigPublicKeyE, bigPublicKeyN);

        if(!TextUtils.isEmpty(stringEncryptedStringInput) && !TextUtils.isEmpty(stringpublicKeyE)
                && !TextUtils.isEmpty(stringpublicKeyN)) {
            String encryptedString = encryptStringToStringOfNum(publicKey, stringEncryptedStringInput);

            encryptedStringOutput.setText(encryptedString);
        }
        encryptedStringInput.setText("");

    }




    private static String encryptStringToStringOfNum(ArrayList<BigInteger> publickey,
                                                     String stringToEncrypt){

        String encryptedStringOfNum = "";
        int length = stringToEncrypt.length();

        for(int i=0; i<length; i++){
            encryptedStringOfNum =  encryptedStringOfNum +
                    encryptCharToNum(publickey,  stringToEncrypt.charAt(i) + "") + "-";
        }

        return encryptedStringOfNum;

    }

    public static ArrayList<BigInteger> getPublicKey(BigInteger e, BigInteger n) {
        ArrayList<BigInteger> publickey = new ArrayList<BigInteger>();
        publickey.add(e);
        publickey.add(n);

        return publickey;

    }

    public static BigInteger encryptCharToNum(ArrayList<BigInteger> publickey,
                                              String charToEncrypt) {
        int charInt = charToInt(charToEncrypt.charAt(0));
        BigInteger encryptedCharBigInt = encryptNumToNum(publickey, charInt);

        return encryptedCharBigInt;
    }

    public static BigInteger encryptNumToNum(ArrayList<BigInteger> publickey,
                                             int intToEncrypt) {
        BigInteger e = publickey.get(0);
        BigInteger n = publickey.get(1);
        BigInteger letterToEncrypt = BigInteger.valueOf(intToEncrypt);

        BigInteger encryptedNum = letterToEncrypt.modPow(e, n);

        return encryptedNum;

    }

    public static int charToInt(char letter) {
        char theLetter = letter;
        if (theLetter == '-')
            return 30;
        return (((int) theLetter) - 95);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
