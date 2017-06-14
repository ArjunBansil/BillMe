package data_objects;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
                ArrayList<Bill> list = new ArrayList<Bill>(); list.add(bill);
                writeList(list);
                Log.i("fun", "File created, added to file list");
            }else{
                Log.i("fun", "Added to file list");
                ArrayList<Bill> list = readList();
                String f = "";
                for(Bill b : list){
                    f += b.getDescription() + "\n";
                }
                Log.i("fun", f);
                list.add(bill);
                writeList(list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Bill> readList(){
        ArrayList<Bill> list = null;
        try{
            File file = new File(context.getFilesDir(), fileName);
            if(!file.exists()){
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Bill>)ois.readObject();
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
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
