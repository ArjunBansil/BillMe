package data_objects;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.keybored.voteme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arjun Bansil on 5/20/2017.
 */

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private List<Bill> billList;
    public Context context;
    public DataHandler dataHandler;

    public BillAdapter(List<Bill> billList, Context context){
        this.context = context;
        this.dataHandler = new DataHandler(this.context);
        this.billList = compare(billList);
    }

    private List<Bill> compare(List<Bill> list){
        ArrayList<Bill> nullList = dataHandler.readList();
        if(nullList == null){
            return list;
        }
        for(Bill b : nullList){
            if (list.contains(b)) {
                list.remove(b);
            }
        }
        return list;
    }

    @Override
    public int getItemCount(){
        return billList.size();
    }

    @Override
    public void onBindViewHolder(BillViewHolder b, int i){
        Bill bill = billList.get(i);
        final BillViewHolder bHolder = b;
        bHolder.chamber.setText(bill.getChamber());
        bHolder.description.setText(bill.getDescription());
        bHolder.leg_day.setText(bill.getLegDay());
        bHolder.consideration.setText(bill.getConsideration());
        final String url = bill.getURL();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        final CustomTabsIntent customTabsIntent = builder.build();
        bHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customTabsIntent.launchUrl(context, Uri.parse(url));
            }
        });

        bHolder.yay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAt(bHolder.getAdapterPosition());
            }
        });

        bHolder.nay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAt(bHolder.getAdapterPosition());
            }
        });

    }

    public void removeAt(int position){
        Bill dump = billList.get(position);
        dataHandler.addToNullList(dump);
        billList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public BillViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.bill_card, viewGroup, false);
        return new BillViewHolder(itemView);
    }


    public static class BillViewHolder extends RecyclerView.ViewHolder{
        protected TextView description, consideration, leg_day, chamber;
        protected Button yay, nay;
        public BillViewHolder(View v){
            super(v);
            description = (TextView)v.findViewById(R.id.description);
            consideration = (TextView)v.findViewById(R.id.consideration);
            leg_day = (TextView)v.findViewById(R.id.leg_day);
            chamber = (TextView)v.findViewById(R.id.chamber);
            yay = (Button)v.findViewById(R.id.vote_yes);
            nay = (Button)v.findViewById(R.id.vote_no);
        }

    }
}
