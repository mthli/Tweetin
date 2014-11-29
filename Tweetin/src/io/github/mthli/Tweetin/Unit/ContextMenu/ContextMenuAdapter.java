package io.github.mthli.Tweetin.Unit.ContextMenu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.mthli.Tweetin.R;

import java.util.List;

public class ContextMenuAdapter extends ArrayAdapter<ContextMenuItem> {
    private Context context;
    private int layoutResId;
    private List<ContextMenuItem> list;

    public ContextMenuAdapter(
            Context context,
            int layoutResId,
            List<ContextMenuItem> list
    ) {
        super(context, layoutResId, list);

        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    private class Holder {
        ImageView icon;
        TextView title;
    }

    @Override
    public View getView(
            final int position,
            final View convertView,
            ViewGroup viewGroup
    ) {
        Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.icon = (ImageView) view.findViewById(R.id.context_menu_item_icon);
            holder.title = (TextView) view.findViewById(R.id.context_menu_item_title);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        ContextMenuItem item = list.get(position);
        holder.icon.setImageDrawable(item.getIcon());
        holder.title.setText(item.getTitle());

        return view;
    }
}
