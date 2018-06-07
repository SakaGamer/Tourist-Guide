package kh.com.touristguide.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import kh.com.touristguide.R;
import kh.com.touristguide.helpers.VolleyRequestQueue;
import kh.com.touristguide.models.Province;

public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ViewHolder> {

    private Context context;
    private List<Province> provinces;
    private ProvinceClickListener provinceClickListener;

    public ProvinceAdapter(Context context, List<Province> provinces) {
        this.context = context;
        this.provinces = provinces;
    }

    public interface ProvinceClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(ProvinceClickListener provinceClickListener){
        this.provinceClickListener = provinceClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_province, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Province province = provinces.get(position);
        holder.textTitle.setText(province.getName());
        ImageLoader imageLoader = VolleyRequestQueue.getInstance(context).getImageLoader();
        holder.imageView.setImageUrl(province.getPhotoUrl(), imageLoader);
    }

    @Override
    public int getItemCount() {
        return provinces.size();
    }

    public void setProvinces(List<Province> provinces){
        this.provinces = provinces;
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {
        private TextView textTitle;
        private NetworkImageView imageView;

        private ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.vh_province_name);
            imageView = itemView.findViewById(R.id.vh_province_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(provinceClickListener != null){
                provinceClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // end main class
}
