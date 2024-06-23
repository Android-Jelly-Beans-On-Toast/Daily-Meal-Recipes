//package com.avivz_gavriels_elyaha.dailymealrecipes;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.DialogFragment;
//
//import android.app.Dialog;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.graphics.Bitmap;
//import android.widget.ImageView;
//
//import androidx.annotation.Nullable;
//
//public class ImageDialogFragment extends DialogFragment {
//
//    private static final String ARG_BITMAP = "bitmap";
//
//    private Bitmap bitmap;
//
//    public static ImageDialogFragment newInstance(Bitmap bitmap) {
//        ImageDialogFragment fragment = new ImageDialogFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(ARG_BITMAP, bitmap);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            bitmap = getArguments().getParcelable(ARG_BITMAP);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_image_dialog, container, false);
//        ImageView imageView = view.findViewById(R.id.imageView);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        }
//        return view;
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }
//}
