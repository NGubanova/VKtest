package com.example.vktest;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<File> item;
    private final ContentResolver contentResolver;


    public RecyclerViewAdapter(Context context, List<File> item) {
        this.item = item;
        this.inflater = LayoutInflater.from(context);
        this.contentResolver = context.getContentResolver();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        // корректный вывод элементов
        Context context = holder.itemView.getContext();
        File file = item.get(position);
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String lastModified = dateFormat.format(new Date(file.lastModified()));
        holder.name.setText(file.getName());
        holder.size.setText(file.length() + " bytes");
        holder.date.setText(lastModified);

        boolean isDirectory = file.isDirectory();

        // отображение иконок
        if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
            long id = getThumbnailId(file.getAbsolutePath());
            if (id != -1) {
                Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                holder.icon.setImageBitmap(thumbnail);
            }
        } else if (isDirectory) {
            holder.icon.setImageResource(R.drawable.ic_folder);
            holder.itemView.setOnClickListener(v -> {
                item.clear();
                item.addAll(Arrays.asList(file.listFiles()));
                notifyDataSetChanged();
            });
        }

        // обновление списка
        if (file.isDirectory()) {
            File[] filesArray = file.listFiles();
            if (filesArray != null) {
                List<File> files = Arrays.asList(filesArray);
                holder.itemView.setOnClickListener(v -> {
                    item.clear();
                    item.addAll(files);
                    notifyDataSetChanged();
                });
            }
        }

        if (file.isDirectory()) {
            holder.icon.setImageResource(R.drawable.ic_folder);
            holder.itemView.setOnClickListener(v -> {
                File[] filesArray = file.listFiles();
                if (filesArray != null) {
                    item.clear();
                    item.addAll(Arrays.asList(filesArray));
                    Collections.sort(item, (f1, f2) -> f1.getName().compareTo(f2.getName()));
                    item.add(0, file.getParentFile());
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.icon.setImageResource(R.drawable.ic_file);
            holder.itemView.setOnClickListener(v -> {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent chooser = Intent.createChooser(intent, "Choose an application to open with:");
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(chooser);
                }
            });
        }

        // намерение открыть файл
        if (!file.isDirectory()) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                intent.setDataAndType(uri, mimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent chooserIntent = Intent.createChooser(intent, "Open with");
                context.startActivity(chooserIntent);
            });
        }

        if (!file.isDirectory()) {
            holder.itemView.setOnLongClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView size;
        TextView date;

        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.iconFile);
            name = view.findViewById(R.id.nameFile);
            size = view.findViewById(R.id.sizeFile);
            date = view.findViewById(R.id.dateFile);
        }
    }

    private long getThumbnailId(String path) {
        long id = -1;
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] selectionArgs = {path};
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            id = cursor.getLong(columnIndex);
            cursor.close();
        }
        return id;
    }

    private void openFile(File file, Context context) {
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
