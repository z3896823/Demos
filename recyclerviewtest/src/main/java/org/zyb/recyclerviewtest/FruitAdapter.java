package org.zyb.recyclerviewtest;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/12.
 *
 * 不同于之前ListView的ViewHolder，这里的ViewHolder直接将findView的过程都封装在了ViewHolder对象生成的时候
 *
 * RecycleView只需要把数据交给Adapter，然后告诉适配器我要装载，其他的工作都由适配器去完成
 * 在这里，将更多的功能封装到了适配器中
 */

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    private List<Fruit> fruitList = new ArrayList<>();

    private String TAG = "adapter";



    public FruitAdapter(List<Fruit> fruitList){
        this.fruitList = fruitList;
    }


    //在这里生成容器(ViewHolder) 然后返回给onBindViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item,parent,false);//inflate的三个方法的重载中的一个，不明白后两个参数什么意思
        final ViewHolder viewHolder = new ViewHolder(view);

        Log.d(TAG, parent.getContext().toString()+"--parent");//三个context是同一个
        Log.d(TAG, view.getContext().toString()+"--view");

        viewHolder.fruitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Fruit fruit = fruitList.get(position);
                Toast.makeText(v.getContext(), fruit.getFruitName()+" image", Toast.LENGTH_SHORT).show();

                Log.d(TAG, v.getContext().toString()+"--v");
            }
        });

        viewHolder.fruitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Fruit fruit = fruitList.get(position);
                Toast.makeText(v.getContext(), fruit.getFruitName()+ " text", Toast.LENGTH_SHORT).show();
            }
        });

        return viewHolder;
    }

    //在这里进行数据的装载
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fruit fruit = fruitList.get(position);
        holder.fruitImage.setImageResource(fruit.getImageId());
        holder.fruitName.setText(fruit.getFruitName());
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView fruitImage;
        TextView fruitName;

        public ViewHolder(View itemView) {
            super(itemView);
            fruitImage = (ImageView) itemView.findViewById(R.id.id_iv_fruitImage);
            fruitName = (TextView) itemView.findViewById(R.id.id_tv_fruitName);
        }
    }
}
