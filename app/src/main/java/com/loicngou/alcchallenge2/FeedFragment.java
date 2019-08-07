package com.loicngou.alcchallenge2;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.loicngou.alcchallenge2.Adapters.FeedRecyclerAdapter;
import com.loicngou.alcchallenge2.Models.Product;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore db ;
    ArrayList<Product> productArrayList = new ArrayList<>();
    RecyclerView feedRecycle;
    FeedRecyclerAdapter feedRecyclerAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View inflatedView;
    private CollectionReference productsRef;
    private Context context;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflatedView = inflater.inflate(R.layout.fragment_feed, container, false);
        this.feedRecycle = inflatedView.findViewById(R.id.feed_recycle_view);
       //        RecyclerView.LayoutManager gridManager = new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL);
        RecyclerView.LayoutManager gridManager = new GridLayoutManager(context,2);
        feedRecycle.setLayoutManager(gridManager);
        feedRecycle.setItemAnimator(new DefaultItemAnimator());
        feedRecyclerAdapter = new FeedRecyclerAdapter(productArrayList,context);
        feedRecycle.setAdapter(feedRecyclerAdapter);
        db = FirebaseFirestore.getInstance();
        productsRef = db.collection("products");

        productsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e!=null){
                    Log.d("ERROR",e.getMessage());
                    return ;
                }

                if (queryDocumentSnapshots != null) {
                    productArrayList.clear();
                    for (DocumentSnapshot documentSnapshot:  queryDocumentSnapshots.getDocuments()
                         ) {
                        if(documentSnapshot != null && documentSnapshot.exists()){
                            Map<String, Object> productMap = documentSnapshot.getData();
                            Log.d("DATA", productMap != null ? productMap.toString() : null);
                            Product p = new Product(
                                    Objects.requireNonNull(productMap != null ? ((productMap.get("product_id")!=null) ?productMap.get("product_id"):"product_id") : "product_id").toString(),
                                    Objects.requireNonNull(productMap != null ? productMap.get("title") : "title").toString(),
                                    Objects.requireNonNull(productMap != null ? productMap.get("description") : "description").toString(),
                                    Double.parseDouble(Objects.requireNonNull(productMap != null ? (productMap.get("price")!=""?productMap.get("price"):"100" ): "100").toString()),
                                    Objects.requireNonNull(productMap != null ? productMap.get("image_url") : "image_url").toString()
                            );
                            productArrayList.add(p);
                        }
                    }
                    feedRecyclerAdapter = new FeedRecyclerAdapter(productArrayList,context);
                    feedRecycle.setAdapter(feedRecyclerAdapter);
                    feedRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        return this.inflatedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
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
