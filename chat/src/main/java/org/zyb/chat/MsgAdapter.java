package org.zyb.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/13.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    List<Msg> mMsg = new ArrayList<>();

    public MsgAdapter(List<Msg> msgs){
        mMsg = msgs;
    }

    //生成容器
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //在这里可以获得点击的item中的对象
        //很奇怪，为什么一定得创建ViewHolder之后才能获取到
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //装载数据
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsg.get(position);
        if (msg.getMsgType() == Msg.TYPE_RECEIVE){
            //左边显示，右边隐藏
            holder.ll_right.setVisibility(View.GONE);
            holder.ll_left.setVisibility(View.VISIBLE);
            holder.tv_receive.setText(msg.getContent());
        } else {
            holder.ll_right.setVisibility(View.VISIBLE);
            holder.ll_left.setVisibility(View.GONE);
            holder.tv_send.setText(msg.getContent());
        }

    }

    @Override
    public int getItemCount() {
        return mMsg.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_receive,tv_send;
        LinearLayout ll_left,ll_right;


        public ViewHolder(View itemView) {
            super(itemView);
            ll_left = (LinearLayout)itemView.findViewById(R.id.id_layout_msgLeft);
            ll_right = (LinearLayout)itemView.findViewById(R.id.id_layout_msgRight);
            tv_receive = (TextView)itemView.findViewById(R.id.id_tv_receive);
            tv_send = (TextView) itemView.findViewById(R.id.id_tv_send);
        }
    }
}
