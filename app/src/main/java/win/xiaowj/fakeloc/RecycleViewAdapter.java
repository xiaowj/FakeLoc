package win.xiaowj.fakeloc;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2017/1/5 005.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<PackageInfo> datas;
    private PackageManager packageManager;

    public RecycleViewAdapter(ArrayList<PackageInfo> datas, Context mContext, PackageManager packageManager) {
        this.datas = datas;
        this.mContext = mContext;
        this.packageManager = packageManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_recycle_view_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PackageInfo info = datas.get(position);
        holder.name.setText(packageManager.getApplicationLabel(info.applicationInfo));
        holder.packageName.setText(info.packageName);
        holder.imageView.setImageDrawable(Utils.getIconByPackageName(packageManager, info.packageName,mContext));

        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(datas.get(position), position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        } else {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, packageName;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            name = (TextView) itemView.findViewById(R.id.name);
            packageName = (TextView) itemView.findViewById(R.id.packageName);
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        public void onClick(PackageInfo packageInfo, int position);
    }

}
