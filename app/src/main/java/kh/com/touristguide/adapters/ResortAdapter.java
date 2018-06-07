package kh.com.touristguide.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.models.Place;

public class ResortAdapter extends RecyclerView.Adapter<ResortAdapter.ViewHolder> {

    private Context context;
    private List<Place> places;
    private ItemClickListener itemClickListener;

    private int viewHolderType;
    private int lastPosition = -1;

    public ResortAdapter(Context context, List<Place> places, int viewHolderType) {
        this.context = context;
        this.places = places;
        this.viewHolderType = viewHolderType;
    }

    @Override
    public int getItemViewType(int position) {
        return this.viewHolderType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.view_holder_place, parent,
                        false);
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.view_holder_place_default, parent,
                        false);
                break;
            case 3:
                view = LayoutInflater.from(context).inflate(R.layout.view_holder_place_grid, parent,
                        false);
                break;
            case 4:
                view = LayoutInflater.from(context).inflate(R.layout.view_holder_place_list, parent,
                        false);
                break;
            case 5:
                view = LayoutInflater.from(context).inflate(R.layout.item_place_grid_no_space, parent,
                        false);
                break;
            default:
                view = LayoutInflater.from(context).inflate(R.layout.view_holder_place_default, parent,
                        false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);
//        double lat = place.getGeoPoint().get("latitude");
//        double lng = place.getGeoPoint().get("longitude");
        holder.textName.setText(place.getName());
        holder.textProvince.setText(place.getProvince());
        Glide.with(context).load(place.getPhotoUrl()).into(holder.imageView);

        Animation animation = AnimationUtils.loadAnimation(context,
                (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setPlaces(List<Place> places){
        this.places = places;
        notifyDataSetChanged();
    }

//    public void remove(Place place){
//        this.places.remove(place);
//    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        private TextView textName;
        private TextView textProvince;
        private ImageView imageView;

        private ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.vh_place_txt_name);
            textProvince = itemView.findViewById(R.id.vh_place_txt_province);
            imageView = itemView.findViewById(R.id.vh_place_img_place);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
