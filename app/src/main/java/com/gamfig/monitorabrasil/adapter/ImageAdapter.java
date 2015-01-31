package com.gamfig.monitorabrasil.adapter;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Politico;

//adapter para o homeFragment
//mostra a foto e nome do deputado
public class ImageAdapter extends BaseAdapter {
	private static final int PADDING = 8;
	private static final int WIDTH = 114;
	private static final int HEIGHT = 152;
	private Activity activity;
	private List<Politico> politicos;

	public ImageAdapter(Activity activity, List<Politico> politicoFavoritos) {
		this.activity = activity;		
		this.politicos = politicoFavoritos;
		// add
		Politico pAdd = new Politico();
		pAdd.setNome("");
		pAdd.setIdCadastro(0);
		this.politicos.add(pAdd);
	}

	@Override
	public int getCount() {
		return politicos.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	// Will get called to provide the ID that
	// is passed to OnItemClickListener.onItemClick()
	// @Override
	// public long getItemId(int position) {
	// return mThumbIds.get(position);
	// }

	// create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ImageView imageView = (ImageView) convertView;
		ViewHolder holder = null;
		LayoutInflater inflator = activity.getLayoutInflater();
		// if convertView's not recycled, initialize some attributes
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflator.inflate(R.layout.item_imagem, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.textView = (TextView) convertView.findViewById(R.id.txtNomeRow);

			convertView.setTag(holder);

			// imageView = new ImageView(mContext);
			// imageView.setLayoutParams(new GridView.LayoutParams(WIDTH,
			// HEIGHT));
			// imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
			// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (politicos.get(position).getIdCadastro() == 0) {
			holder.imageView.setImageResource(R.drawable.ic_action_add);
			
		} else {
			// get Congresmans photo
			Bitmap bitmap = new UserDAO(activity).buscaFotoCache(politicos.get(position).getIdCadastro());
			if ( bitmap != null) {
				
				holder.imageView.setImageBitmap(bitmap);
			} else {
				
				holder.imageView.setImageBitmap(null);
				ImageGridHandler handler = new ImageGridHandler(activity, holder.imageView);
				handler.execute(String.valueOf(politicos.get(position).getIdCadastro()));
			}

		}

		// name
		holder.textView.setText(politicos.get(position).getNome());
		holder.textView.setTextColor(Color.WHITE);

		// id
		holder.id = politicos.get(position).getIdCadastro();

		return convertView;

		// imageView.setImageResource(mThumbIds.get(position));
		// return imageView;
	}

	private class ViewHolder {
		// The position of this row in list
		private int position;

		// The image view for each row
		private ImageView imageView;

		// The textView for each row
		private TextView textView;

		// The Congressmans id
		private int id;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ImageGridHandler extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private Context context;
		int idDeputado;

		public ImageGridHandler(Context context, ImageView img) {
			imageViewReference = new WeakReference<ImageView>(img);
			this.context = context;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			idDeputado = Integer.valueOf(params[0]);
			return Imagens.getImageBitmap( params[0] );

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			final ImageView imageView = imageViewReference.get();
			if (result != null) {
				try {
					imageView.setImageBitmap(result);
					// save congressmans photo
					UserDAO userDAO = new UserDAO(context);
					userDAO.salvaFotoDeputado(idDeputado, result);
				} catch (Exception e) {
					Toast.makeText(context, "Falha ao carregar imagens", Toast.LENGTH_SHORT).show();
				}
				
			} else {
				Toast.makeText(context, "Falha ao carregar imagens", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
