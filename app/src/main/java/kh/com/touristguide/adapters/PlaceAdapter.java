package kh.com.touristguide.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.VolleyRequestQueue;
import kh.com.touristguide.models.Place;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<Place> places;
    private Context context;
    private PlaceClickListener placeClickListener;

    public PlaceAdapter(Context context, List<Place> places) {
        this.places = places;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_place,
                parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.name.setText(place.getName());
        ImageLoader imageLoader = VolleyRequestQueue.getInstance(context).getImageLoader();
        imageLoader.get(place.getPhotoUrl(), ImageLoader.getImageListener(holder.imageView,
                R.drawable.ic_image_default, R.drawable.ic_image_default));
        holder.imageView.setImageUrl(place.getPhotoUrl(), imageLoader);

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        if (place.getSavedBy().containsValue(uid)) {
//            holder.imageStar.setImageResource(R.drawable.ic_heart_filled);
//        } else {
//            holder.imageStar.setImageResource(R.drawable.ic_heart_filled);
//        }
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void setOnClickListener(PlaceClickListener placeClickListener) {
        this.placeClickListener = placeClickListener;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }

    public interface PlaceClickListener {
        void onItemClick(View view, int position);
    }

    protected class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        NetworkImageView imageView;

        private PlaceViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vh_place_txt_name);
            imageView = itemView.findViewById(R.id.vh_place_img_place);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (placeClickListener != null) {
                placeClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
