package com.loicngou.alcchallenge2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static  final int GALLERY_REQUEST_CODE =1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View inflatedView;
    private Button chooseImageBtn;
    private ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore firestore ;
    private Button saveBtn;
    private ProgressBar progressBar;
    private TextView progression;
    private TextInputEditText title;
    private TextInputEditText description;
    private TextInputEditText price;
    private Context context;


    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = Objects.requireNonNull(getActivity()).getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         inflatedView = inflater.inflate(R.layout.fragment_add_product, container, false);
        this.bindData();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        this.chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductFragment.this.pickFromGallery();
            }
        });

        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(Objects.requireNonNull(title.getText()).toString()) || TextUtils.isEmpty(Objects.requireNonNull(description.getText()).toString()) || TextUtils.isEmpty(Objects.requireNonNull(price.getText()).toString())){
                    Toast.makeText(context,"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        Double.parseDouble(price.getText().toString());
                        uploadImage();
                    }catch(Exception e){
                        Toast.makeText(context,"Veuillez entrer un entier",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        return inflatedView ;
    }

    public void bindData(){
        this.chooseImageBtn = this.inflatedView.findViewById(R.id.choosePickBtn);
        this.imageView = this.inflatedView.findViewById(R.id.imageView);
        this.saveBtn = this.inflatedView.findViewById(R.id.save_btn);
        this.progressBar = this.inflatedView.findViewById(R.id.progressBar);
        this.progression = this.inflatedView.findViewById(R.id.progression);
        this.title = this.inflatedView.findViewById(R.id.product_title);
        this.description = this.inflatedView.findViewById(R.id.product_description);
        this.price= this.inflatedView.findViewById(R.id.product_price);

    }
    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void uploadImage() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
        Toast.makeText(context, "Upload Started", Toast.LENGTH_SHORT).show();
        UploadTask uploadTask =ref.putBytes(data);
        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.w("DOWNLOAD",uri.toString());
                                Map<String ,Object> data = new HashMap<>();
                                String id = UUID.randomUUID().toString();
                                data.put("title",AddProductFragment.this.title.getText().toString());
                                data.put("description",AddProductFragment.this.description.getText().toString());
                                data.put("price",AddProductFragment.this.price.getText().toString());
                                data.put("image_url",uri.toString());
                                data.put("product_id",id);

                                AddProductFragment.this.firestore
                                    .collection("products")
                                    .document(id)
                                    .set(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                AddProductFragment.this.resetFields();
                                            }
                                        }
                                    });
                            }
                        });
                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());

                        AddProductFragment.this.progressBar.setProgress((int)progress);
                        AddProductFragment.this.progression.setText((int)progress +"% ");
                    }
                });

    }

    private void resetFields() {
        this.title.setText("");
        this.description.setText("");
        this.price.setText("");
        this.imageView.setImageResource(0);
        this.progressBar.setProgress(0);
        this.progression.setText("0 %");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    break;
                default:
                    break;
            }
        } else{
            Toast.makeText(context,"You must select an image",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
