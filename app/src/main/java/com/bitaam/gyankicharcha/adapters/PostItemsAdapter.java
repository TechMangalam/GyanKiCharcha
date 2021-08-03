package com.bitaam.gyankicharcha.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bitaam.gyankicharcha.R;
import com.bitaam.gyankicharcha.modals.ChatViewTypeModel;
import com.bitaam.gyankicharcha.modals.PostModel;
import com.bitaam.gyankicharcha.utillity.Encryptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;

public class PostItemsAdapter extends RecyclerView.Adapter {

    ArrayList<PostModel> postModels;
    Context context;
    boolean isExpanded = false;
    Encryptions encryptions;

    public PostItemsAdapter(ArrayList<PostModel> postModels, Context context) {
        this.postModels = postModels;
        this.context = context;
        encryptions = new Encryptions();

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case PostModel.TEXT_TYPE:
            case PostModel.IMAGE_TYPE:
            case PostModel.IMAGE_VIDEO_TYPE:
            case PostModel.DOCUMENT_IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_view,parent,false);
                return new PostViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();

        ((PostViewHolder)holder).player  = new SimpleExoPlayer.Builder(context).build();

        if (!postModels.isEmpty()){
            PostModel postModel = postModels.get(position);
            String msg = postModel.getPostText();
            switch (postModel.getPostType()){
                case PostModel.TEXT_TYPE:
                case PostModel.IMAGE_TYPE:
                case PostModel.IMAGE_VIDEO_TYPE:
                case PostModel.DOCUMENT_IMAGE_TYPE:
                    ((PostViewHolder)holder).profileNameTv.setText(encryptions.getDecreption(postModel.getProfileName(),postModel.getPostAuthId(),postModel.getPostAuthId()));
                    ((PostViewHolder)holder).postDateTv.setText(postModel.getPostDate());
                    Glide.with(context).load(postModel.getProfileImageUrl()).circleCrop().placeholder(R.drawable.profile_icon).into(((PostViewHolder)holder).profileCircularImgView);
                    /*if ( postModel.getOfflineDataUrls() != null && new File(postModel.getOfflineDataUrls().get(0)).exists()){
                        ((PostViewHolder)holder).postImgView.setImageURI(Uri.parse(postModel.getOfflineDataUrls()));
                    }else*/ if(postModel.getDataUrls() != null){
                        if (postModel.getPostType() == PostModel.IMAGE_TYPE){
                            if (((PostViewHolder)holder).player.isPlaying()){
                                ((PostViewHolder)holder).player.stop();
                            }
                            ((PostViewHolder)holder).exoPlayerView.setVisibility(View.GONE);
                            ((PostViewHolder)holder).postImgView.setVisibility(View.VISIBLE);
                            Glide.with(context).load(postModel.getDataUrls()).apply(options).into(((PostViewHolder)holder).postImgView);
                        }else{
                            ((PostViewHolder)holder).exoPlayerView.setPlayer(((PostViewHolder)holder).player);
                            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(postModel.getDataUrls()));
                            ((PostViewHolder)holder).player.setMediaItem(mediaItem);
                            ((PostViewHolder)holder).exoPlayerView.setVisibility(View.VISIBLE);
                            ((PostViewHolder)holder).postImgView.setVisibility(View.GONE);
                            ((PostViewHolder)holder).player.prepare();
                            ((PostViewHolder)holder).exoPlayerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                                @Override
                                public void onViewAttachedToWindow(View v) {
                                    Objects.requireNonNull(((PostViewHolder) holder).exoPlayerView.getPlayer()).prepare();

                                }

                                @Override
                                public void onViewDetachedFromWindow(View v) {
                                    Objects.requireNonNull(((PostViewHolder) holder).exoPlayerView.getPlayer()).stop();
                                }
                            });
                        }
                    }else {
                        ((PostViewHolder)holder).exoPlayerView.setVisibility(View.GONE);
                        ((PostViewHolder)holder).postImgView.setVisibility(View.GONE);
                    }
                    ((PostViewHolder)holder).postMsgTv.setText(postModel.getPostText());
                    ((PostViewHolder)holder).ExpandCollapse(((PostViewHolder)holder).postMsgTv,((PostViewHolder)holder).itemView);

//                    long interval = 2 * 1000;
//                    RequestOptions options = new RequestOptions().frame(interval);
//                    Glide.with(context).asBitmap()
//                            .load(Uri.parse("https://firebasestorage.googleapis.com/v0/b/gyankicharcha-2c0af.appspot.com/o/raining5-7-21.mp4?alt=media&token=a0e496a1-d160-4c08-aab9-f92b35ac8dff"))
//                            .apply(options)
//                            .into(((PostViewHolder)holder).postImgView);

                    break;
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        switch (postModels.get(position).getPostType()){
            case 0:
                return PostModel.TEXT_TYPE;
            case 1:
                return PostModel.IMAGE_TYPE;
            case 2:
                return PostModel.IMAGE_VIDEO_TYPE;
            case 3:
                return PostModel.DOCUMENT_IMAGE_TYPE;
            case 4:
                return PostModel.GOOGLE_ADS_TYPE;
            case 5:
                return PostModel.CUSTOM_ADS_TYPE;
            default:
                return -1;
        }

    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public void update(PostModel model){
        postModels.add(model);
        notifyDataSetChanged();
    }

    public  class PostViewHolder extends RecyclerView.ViewHolder {

        TextView postMsgTv,postDateTv,profileNameTv;
        ImageView postImgView,moreMenuImgView;
        ImageButton likeImgBtn,forwardImgBtn,commentImgBtn,bookmarkImgBtn;
        CircleImageView profileCircularImgView;
        PlayerView exoPlayerView;
        SimpleExoPlayer player;


        public PostViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            postMsgTv = itemView.findViewById(R.id.post_text_view);
            postDateTv = itemView.findViewById(R.id.post_date_tv);
            profileNameTv = itemView.findViewById(R.id.sender_profile_name_tv);
            postImgView = itemView.findViewById(R.id.post_image_view);
            exoPlayerView = itemView.findViewById(R.id.exoplayer_post);
            moreMenuImgView = itemView.findViewById(R.id.post_more_menu);
            profileCircularImgView = itemView.findViewById(R.id.sender_profile_circular_iv);
            likeImgBtn = itemView.findViewById(R.id.post_like_img_btn);
            commentImgBtn = itemView.findViewById(R.id.post_comment_img_btn);
            forwardImgBtn = itemView.findViewById(R.id.post_forward_img_btn);
            bookmarkImgBtn = itemView.findViewById(R.id.post_bookmark_img_btn);



            likeImgBtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    likeImgBtn.setImageResource(R.drawable.ic_baseline_thumb_up_active_24);
                }
            });



        }

        private void Ellipsize(boolean activate, TextView textView){

            if (activate) {
                textView.setMaxLines(4);
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
            else{
                //textView.setSingleLine(false);
                textView.setMaxLines(15);
                textView.setEllipsize(null);
            }
        }

        private void ExpandCollapse(final TextView textView, final View view){

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded) {
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(false, textView);
                    }
                    else{
                        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView(), new AutoTransition());
                        Ellipsize(true, textView);
                    }
                    isExpanded = !isExpanded;
                }
            });

        }


    }

}
