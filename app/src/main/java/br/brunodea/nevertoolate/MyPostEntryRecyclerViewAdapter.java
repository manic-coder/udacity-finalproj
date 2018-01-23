package br.brunodea.nevertoolate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import br.brunodea.nevertoolate.PostEntryListFragment.OnListFragmentInteractionListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPostEntryRecyclerViewAdapter extends RecyclerView.Adapter<MyPostEntryRecyclerViewAdapter.ViewHolder> {
    private static String TAG = "MyPostEntryRecyclerViewAdapter";

    private Context mContext;
    private Listing<Submission> mRedditPosts;
    private final OnListFragmentInteractionListener mListener;
    private OnPostImageClickListener mOnPostImageClickListener;

    // TODO: make sure reddit_posts only contain posts with images in the URL
    public MyPostEntryRecyclerViewAdapter(Context context, Listing<Submission> reddit_posts,
                                          OnListFragmentInteractionListener listener,
                                          OnPostImageClickListener postImageClickListener) {
        mContext = context;
        mRedditPosts = reddit_posts;
        mListener = listener;
        mOnPostImageClickListener = postImageClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_postentry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mRedditPost = mRedditPosts.get(position);
        holder.mTVDescription.setText(holder.mRedditPost.getTitle());
        holder.mIVActionExpand.setOnClickListener(view -> {
            String tag_down = mContext.getString(R.string.card_action_expand_tag_down);
            String tag_up = mContext.getString(R.string.card_action_expand_tag_up);
            if (holder.mIVActionExpand.getTag().equals(tag_down)) {
                holder.mIVActionExpand.setImageResource(R.drawable.ic_expand_up_24dp);
                holder.mIVActionExpand.setTag(tag_up);
                holder.mExpandableLayout.expand();
            } else {
                holder.mIVActionExpand.setImageResource(R.drawable.ic_expand_down_24dp);
                holder.mIVActionExpand.setTag(tag_down);
                holder.mExpandableLayout.toggle();
            }
        });
        holder.mIVActionFavorite.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onActionFavorite(holder.mRedditPost);
            }
        });
        holder.mIVActionReddit.setOnClickListener(view ->{
            if (mListener != null) {
                mListener.onActionReddit(holder.mRedditPost);
            }
        });
        holder.mIVActionShare.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onActionShare(holder.mRedditPost);
            }
        });

        holder.mIVPostImage.setOnClickListener(view1 -> {
            if (mOnPostImageClickListener != null) {
                mOnPostImageClickListener.onClick(holder.mIVPostImage);
            }
        });
        String url = holder.mRedditPost.getUrl();
        if (url.contains("imgur")) {
            // If the link is for imgur, we need to change it to the address of the image location itself.
            // By appending a lowercase L to the imgur's image hash, we get a smaller image
            if (url.contains("/imgur")) {
                url = url.replace("/imgur", "/i.imgur");
                url += "l.jpg";
            } else {
                String ext = url.substring(url.lastIndexOf("."), url.length());
                String x_ext = "l" + ext;
                url = url.replace(ext, x_ext);
            }
        }

        Log.i(TAG, url);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(mContext.getResources().getColor(android.R.color.white));
        holder.mIVPostImage.setImageDrawable(gradientDrawable);
        Picasso.with(mContext)
                .load(url)
                .placeholder(gradientDrawable)
                .into(holder.mIVPostImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mPBLoadingImage.setVisibility(View.GONE);
                        holder.mIVPostImage.setVisibility(View.VISIBLE);
                        holder.mImageErrorLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.mPBLoadingImage.setVisibility(View.GONE);
                        holder.mIVPostImage.setVisibility(View.GONE);
                        holder.mImageErrorLayout.setVisibility(View.VISIBLE);

                        Bitmap bitmap = ((BitmapDrawable)holder.mIVPostImage.getDrawable()).getBitmap();
                        final double viewWidthToBitmapWidthRatio = (double)holder.mIVPostImage.getWidth() / (double)bitmap.getWidth();
                        ViewGroup.LayoutParams params = holder.mIVPostImage.getLayoutParams();
                        params.height = (int) (bitmap.getHeight() * viewWidthToBitmapWidthRatio);
                        holder.mIVPostImage.setLayoutParams(params);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mRedditPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_card_post_container) CardView mCardView;
        @BindView(R.id.cl_post_container) ConstraintLayout mCLPostContainer;
        @BindView(R.id.image_error_layout) LinearLayout mImageErrorLayout;
        @BindView(R.id.iv_post_image) ImageView mIVPostImage;
        @BindView(R.id.iv_post_favorite) ImageView mIVActionFavorite;
        @BindView(R.id.iv_post_reddit) ImageView mIVActionReddit;
        @BindView(R.id.iv_post_share) ImageView mIVActionShare;
        @BindView(R.id.iv_post_expand) ImageView mIVActionExpand;
        @BindView(R.id.tv_post_description) TextView mTVDescription;
        @BindView(R.id.pb_loading_image) ProgressBar mPBLoadingImage;
        @BindView(R.id.expandable_layout) ExpandableLayout mExpandableLayout;

        private Submission mRedditPost;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnPostImageClickListener {
        void onClick(ImageView imageView);
    }
}
