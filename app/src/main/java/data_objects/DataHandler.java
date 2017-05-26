package data_objects;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Arjun Bansil on 5/25/2017.
 */

public class DataHandler implements Serializable{
    private Context context;
    private static final String fileName = "null_list.txt";
    private File file;

    public DataHandler(Context context){
        this.context = context;
    }

    public void addToNullList(Bill bill){
        try{
            file = new File(context.getFilesDir(), fileName);
            if(!file.exists()){
                Log.i("fun", "File doesn't exist ATM");
                boolean f = file.mkdir();
                if (f){
                    Log.i("fun", "File does exist now");
                }
                ArrayList<Bill> list = new ArrayList<Bill>(); list.add(bill);
                writeList(list);
            }else{
                ArrayList<Bill> list = readList();
                list.add(bill);
                writeList(list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Bill> readList(){
        try{
            FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), fileName));
            ObjectInputStream ois = new ObjectInputStream(fis);
            if(ois.readObject() instanceof ArrayList){
                return (ArrayList<Bill>)ois.readObject();
            }else return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void writeList(ArrayList<Bill> list){
        try {
            FileOutputStream fos = new FileOutputStream(new  File(context.getFilesDir(), fileName));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete(){
        try{
            File file = new File(context.getFilesDir(), fileName);
            file.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
