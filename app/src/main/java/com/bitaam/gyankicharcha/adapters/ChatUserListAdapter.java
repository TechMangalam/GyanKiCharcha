package com.bitaam.gyankicharcha.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.gyankicharcha.ChatActivity;
import com.bitaam.gyankicharcha.R;
import com.bitaam.gyankicharcha.modals.ChatFriendModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatFriendModel> friendModels ;
    Context context;
    String myNo;

    public ChatUserListAdapter(ArrayList<ChatFriendModel> friendModels, Context context, String myNo) {
        this.friendModels = friendModels;
        this.context = context;
        this.myNo = myNo;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_user_item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        ChatFriendModel model = friendModels.get(position);

        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.recentMsgTv.setText(model.getRecentMsg());
        viewHolder.nameTv.setText(model.getFriendName());
        viewHolder.userItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("FriendName",model.getFriendName());
                intent.putExtra("FriendNo",model.getFriendNo());
                intent.putExtra("MyNo",myNo);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendModels.size();
    }

    public void update(ChatFriendModel model,String mNo){

        friendModels.add(model);
        myNo = mNo;
        notifyDataSetChanged();

    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTv,recentMsgTv;
        CircleImageView profileImageView;
        RelativeLayout userItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.profileName);
            recentMsgTv = itemView.findViewById(R.id.statusTv);
            profileImageView = itemView.findViewById(R.id.profile_image);
            userItemLayout  = itemView.findViewById(R.id.userInfoLayout);


//            if(userRole.equals("Developer")){
//                //upcharEditImgView.setVisibility(View.VISIBLE);
//            }

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = recyclerView.getChildLayoutPosition(view);
                    Intent intent = new Intent(context, UpcharDisplayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("patientInfo", (Serializable) patientModels.get(position));
                    context.startActivity(intent);

                }
            }); */

        }
    }

}
