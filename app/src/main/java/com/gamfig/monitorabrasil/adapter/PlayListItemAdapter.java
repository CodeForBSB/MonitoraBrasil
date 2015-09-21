package com.gamfig.monitorabrasil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.AppController;
import com.gamfig.monitorabrasil.classes.youtube.PlayList;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PlayListItemAdapter extends ArrayAdapter<PlayList> {
	private Context context;
	private List<PlayList> list;
    int layoutResourceId;
    private ImageLoader mImagemLoader;


	public PlayListItemAdapter(Context context, int layoutResourceId, List<PlayList> list){
        super(context, layoutResourceId, list);
		this.context = context;
		this.list = list;
        this.layoutResourceId = layoutResourceId;
        this.mImagemLoader = AppController.getInstance().getmImagemLoader();
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VideoHolder holder = null;

        if (row == null) {
            LayoutInflater infater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infater.inflate(layoutResourceId, parent, false);
            holder = new VideoHolder();

            holder.txtDescricao = (TextView) row.findViewById(R.id.txtDescricao);
            holder.txtData = (TextView) row.findViewById(R.id.txtData);
            holder.imgVideo = (ImageView) row.findViewById(R.id.imgVideo);
            row.setTag(holder);
        } else {
            holder = (VideoHolder) row.getTag();
        }
        PlayList video = list.get(position);

        holder.id = video.getId();
        holder.txtDescricao.setText(video.getTitulo());
        holder.txtData.setText(video.getData());

        mImagemLoader.displayImage(video.getThumb(), holder.imgVideo);

        return row;
    }

    static class VideoHolder {

        TextView txtDescricao;
        TextView txtData;
        String id;
        ImageView imgVideo;
    }
	
}
