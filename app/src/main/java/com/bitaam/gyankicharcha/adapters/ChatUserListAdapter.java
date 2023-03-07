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
import com.squareup.picasso.Picasso;

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
        String profileImages[] = new String[8];
        profileImages[0] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2FAyurveda-1.jpg?alt=media&token=1b618fa7-6f64-4bf2-849a-7d21758f72f5";
        profileImages[1] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2FScreenshot%202021-07-04%20000437.png?alt=media&token=44f5e57d-bbc7-494f-a83e-2c7cc845a664";
        profileImages[2] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2Fcovid.png?alt=media&token=e5ab00cc-2279-43fc-ac57-b8d6671be0c8";
        profileImages[3] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2Fexercise.jpg?alt=media&token=229fae9d-5a37-4a6a-b27c-bee937887f6f";
        profileImages[4] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2Fimgonline-com-ua-compressed-IrPoeoge4EFMWkbq.jpg?alt=media&token=cd051766-9c4f-4406-91a3-7009dfb1ba0f";
        profileImages[5] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2Fphoto.jpg?alt=media&token=35bef431-c573-4d3b-bbb9-6dae979737af";
        profileImages[6] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2Fprofile.jfif?alt=media&token=3a948dfe-78c6-4894-b488-07a4e943fdf9";
        profileImages[7] = "https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/dummyImages%2Fdepositphotos_59095529-stock-illustration-profile-icon-male-avatar.jpg?alt=media&token=2067490b-c269-4a0c-80f5-916df5253572";



        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.recentMsgTv.setText(model.getRecentMsg());
        viewHolder.nameTv.setText(model.getFriendName());
        Picasso.get().load(profileImages[position]).fit().into(viewHolder.profileImageView);
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
