package com.bitaam.gyankicharcha.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bitaam.gyankicharcha.R;
import com.bitaam.gyankicharcha.modals.ChatViewTypeModel;
import com.bitaam.gyankicharcha.utillity.Encryptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTelegram;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class ChatItemsAdapter extends RecyclerView.Adapter {

    ArrayList<ChatViewTypeModel> chatViewTypeModels ;
    Context mContext;
    private ClickAdapterListener listener;
    private SparseBooleanArray selectedItems;


    private static int currentSelectedIndex = -1;



    public interface ClickAdapterListener {

        void onRowClicked(int position);

        void onRowLongClicked(int position);
    }


    public ChatItemsAdapter(ArrayList<ChatViewTypeModel> chatViewTypeModels, Context mContext, ClickAdapterListener listener, SparseBooleanArray selectedItems) {
        this.chatViewTypeModels = chatViewTypeModels;
        this.mContext = mContext;
        this.listener = listener;
        this.selectedItems = selectedItems;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType){
            case ChatViewTypeModel.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_text_msg_view,parent,false);
                return new ChatTextTypeViewHolder(view);
            case ChatViewTypeModel.IMAGE_TYPE:
            case ChatViewTypeModel.IMAGE_VIDEO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_img_view,parent,false);
                return new ChatImageTypeViewHolder(view);
            case ChatViewTypeModel.DOCUMENT_IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_document_view,parent,false);
                return new ChatDocumentTypeViewHolder(view);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        ChatViewTypeModel model = chatViewTypeModels.get(position);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Encryptions encryptions = new Encryptions();
        int nightModeFlags =
                mContext.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;



        if (model!=null){
            switch (model.type){
                case ChatViewTypeModel.TEXT_TYPE:

                    ((ChatTextTypeViewHolder)holder).richLinkView.setVisibility(View.GONE);


                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)((ChatTextTypeViewHolder)holder).rl2.getLayoutParams();

                    if (model.authId.equals(uid)){
                        params1.removeRule(RelativeLayout.ALIGN_PARENT_START);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_END);
                        ((ChatTextTypeViewHolder)holder).rl2.setLayoutParams(params1);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
                            ((ChatTextTypeViewHolder)holder).rl2.setBackgroundResource(R.drawable.msg_bg_dark_mode_self_msg);
                        }else{
                            ((ChatTextTypeViewHolder)holder).rl2.setBackgroundResource(R.drawable.round_corner_7dp_no_stroke_teel_white_bg);
                        }
                    }else{
                        params1.removeRule(RelativeLayout.ALIGN_PARENT_END);
                        params1.addRule(RelativeLayout.ALIGN_PARENT_START);
                        ((ChatTextTypeViewHolder)holder).rl2.setLayoutParams(params1);
                        ((ChatTextTypeViewHolder)holder).rl2.setBackgroundResource(R.drawable.round_bg_no_stroke_teal700);

                    }
                    if (URLUtil.isValidUrl(model.text)){
                        ((ChatTextTypeViewHolder)holder).richLinkView.setLink(model.text, new ViewListener() {
                            @Override
                            public void onSuccess(boolean status) {
                                if (status){
                                    ((ChatTextTypeViewHolder)holder).richLinkView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }
                    ((ChatTextTypeViewHolder)holder).chatMsgTv.setText(model.text);
                    ((ChatTextTypeViewHolder)holder).chatTimeTv.setText(model.dateTime);
                    ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            if (((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.isSelected()){
                                ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setSelected(false);
                                ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setBackgroundColor(0);
                            }else{
                                ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setSelected(true);
                                ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.item_selecter_color));
                            }
                            listener.onRowLongClicked(position);
                            //((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setSelected(true);
                            //((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.item_selecter_color));
                            return true;
                        }
                    });
                    ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getSelectedItemCount()>0){

                                if (((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.isSelected()){
                                    ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setSelected(false);
                                    ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setBackgroundColor(0);
                                }else{
                                    ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setSelected(true);
                                    ((ChatTextTypeViewHolder)holder).chatTextRelativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.item_selecter_color));
                                }
                                listener.onRowClicked(position);
                            }
                        }
                    });
                    break;
                case ChatViewTypeModel.IMAGE_TYPE:
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)((ChatImageTypeViewHolder)holder).rl2.getLayoutParams();

                    if (model.authId.equals(uid)){
                        params2.removeRule(RelativeLayout.ALIGN_PARENT_START);
                        params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                        ((ChatImageTypeViewHolder)holder).rl2.setLayoutParams(params2);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
                            ((ChatImageTypeViewHolder)holder).rl2.setBackgroundResource(R.drawable.msg_bg_dark_mode_self_msg);
                        }else{
                            ((ChatImageTypeViewHolder)holder).rl2.setBackgroundResource(R.drawable.round_corner_7dp_no_stroke_teel_white_bg);
                        }
                    }else{
                        params2.removeRule(RelativeLayout.ALIGN_PARENT_END);
                        params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                        ((ChatImageTypeViewHolder)holder).rl2.setLayoutParams(params2);
                        ((ChatImageTypeViewHolder)holder).rl2.setBackgroundResource(R.drawable.round_bg_no_stroke_teal700);

                    }
                    ((ChatImageTypeViewHolder)holder).chatMsgTv.setText(model.text);
                    ((ChatImageTypeViewHolder)holder).chatTimeTv.setText(model.dateTime);
                    Glide.with(mContext).load(model.dataUrl).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((ChatImageTypeViewHolder)holder).imgProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(((ChatImageTypeViewHolder)holder).chatImageView);
                    break;
                case ChatViewTypeModel.IMAGE_VIDEO_TYPE:
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(model.dataUrl , MediaStore.Video.Thumbnails.MICRO_KIND);
                    bMap = transformBitmapToCircularBitmap(bMap);
                    if (model.authId.equals(uid)){
                        ((ChatImageTypeViewHolder)holder).chatImageRelativeLayout.setGravity(Gravity.END);
                    }else{
                        ((ChatImageTypeViewHolder)holder).chatImageRelativeLayout.setGravity(Gravity.START);

                    }
                    ((ChatImageTypeViewHolder)holder).chatImageView.setImageBitmap(bMap);
                    ((ChatImageTypeViewHolder)holder).chatImageView.setEnabled(false);
                    ((ChatImageTypeViewHolder)holder).videoIconImgView.setVisibility(View.VISIBLE);
                    ((ChatImageTypeViewHolder)holder).chatMsgTv.setText(model.text);
                    ((ChatImageTypeViewHolder)holder).chatTimeTv.setText(model.dateTime);
                    break;
                case ChatViewTypeModel.DOCUMENT_IMAGE_TYPE:
                    if (model.authId.equals(uid)){
                        ((ChatDocumentTypeViewHolder)holder).documentRelativeLayout.setGravity(Gravity.RIGHT);
                    }else{
                        ((ChatDocumentTypeViewHolder)holder).documentRelativeLayout.setGravity(Gravity.LEFT);

                    }
                    ((ChatDocumentTypeViewHolder)holder).documentNameTv.setText(model.text);
                    ((ChatDocumentTypeViewHolder)holder).documentTimeTv.setText(model.dateTime);
                    ((ChatDocumentTypeViewHolder)holder).documentRelativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(model.dataUrl), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Intent newIntent = Intent.createChooser(intent, "Open File");
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                mContext.startActivity(newIntent);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                            }
                        }
                    });
                    break;


            }
        }

    }




    @Override
    public int getItemCount() {
        return chatViewTypeModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatViewTypeModels.get(position).type){
            case 0:
                return ChatViewTypeModel.TEXT_TYPE;
            case 1:
                return ChatViewTypeModel.IMAGE_TYPE;
            case 2:
                return ChatViewTypeModel.IMAGE_VIDEO_TYPE;
            case 3:
                return ChatViewTypeModel.DOCUMENT_IMAGE_TYPE;
            default:
                return -1;
        }
    }

    public void update(ChatViewTypeModel model){
        chatViewTypeModels.add(model);
        notifyDataSetChanged();
    }

    public  Bitmap transformBitmapToCircularBitmap(Bitmap source)
    {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void selectAll() {

        for (int i = 0; i < getItemCount(); i++)
            selectedItems.put(i, true);
        notifyDataSetChanged();

    }


    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List getSelectedItems() {
        List items =
                new ArrayList(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        chatViewTypeModels.remove(position);
        resetCurrentIndex();
    }

    public void updateData(int position) {
        //chatViewTypeModels.get(position).colored = true;
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }


        public  class ChatTextTypeViewHolder extends RecyclerView.ViewHolder {

        TextView chatMsgTv;
        TextView chatTimeTv;
        RelativeLayout chatTextRelativeLayout,rl2;
        RichLinkView richLinkView;

        public ChatTextTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            chatMsgTv = itemView.findViewById(R.id.textMsgTv);
            chatTimeTv = itemView.findViewById(R.id.msg_time_date_Tv);
            chatTextRelativeLayout = itemView.findViewById(R.id.chat_txt_msg_layout);
            rl2 = itemView.findViewById(R.id.rl2);
            richLinkView = itemView.findViewById(R.id.richLinkView);


        }
    }

    public  class ChatImageTypeViewHolder extends RecyclerView.ViewHolder {

        TextView chatMsgTv;
        TextView chatTimeTv;
        ImageView chatImageView,videoIconImgView;
        RelativeLayout rl2;
        RelativeLayout chatImageRelativeLayout;
        ProgressBar imgProgressBar;

        public ChatImageTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            rl2 = itemView.findViewById(R.id.rl2);
            chatMsgTv = itemView.findViewById(R.id.textMsgTv);
            chatTimeTv = itemView.findViewById(R.id.msg_time_date_Tv);
            chatImageView = itemView.findViewById(R.id.chat_imgview);
            videoIconImgView = itemView.findViewById(R.id.chat_video_icon_img_view);
            chatImageRelativeLayout = itemView.findViewById(R.id.chat_image_layout);
            imgProgressBar = itemView.findViewById(R.id.img_progress_bar);

        }
    }

        public  class ChatDocumentTypeViewHolder extends RecyclerView.ViewHolder {

            TextView documentNameTv;
            TextView documentTimeTv;
            TextView documentTypeTv;
            ImageView documentTypeImageView;
            RelativeLayout documentRelativeLayout;

            public ChatDocumentTypeViewHolder(@NonNull View itemView) {
                super(itemView);

                documentNameTv = itemView.findViewById(R.id.document_name_tv);
                documentTimeTv = itemView.findViewById(R.id.document_time_date_Tv);
                documentTypeTv = itemView.findViewById(R.id.document_type_tv);
                documentTypeImageView = itemView.findViewById(R.id.chat_imgview);
                documentRelativeLayout = itemView.findViewById(R.id.chatDocumentLayout);



            }

    }



}
