package com.inquilinosrd.inquilinosmobile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResidenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResidenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidenceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public TextView addressTV, roomsTV, bathTV, parkTV, priceTV;
    public ImageView mainImageTV;
    public FrameLayout frameLayout;
    public LinearLayout mainLinearLayout;
    public ImageView closePopup_btn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResidenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResidenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResidenceFragment newInstance(String param1, String param2) {
        ResidenceFragment fragment = new ResidenceFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_residence, container, false);
        addressTV = (TextView) itemView.findViewById(R.id.addressTextView);
        roomsTV = (TextView) itemView.findViewById(R.id.roomsTextView);
        bathTV = (TextView) itemView.findViewById(R.id.bathTextView);
        parkTV = (TextView) itemView.findViewById(R.id.parkTextView);
        priceTV = (TextView) itemView.findViewById(R.id.priceTextView);
        mainImageTV = (ImageView) itemView.findViewById(R.id.selectedHouseImage);
        frameLayout = (FrameLayout) itemView.findViewById(R.id.residenceFrameLayout);
        mainLinearLayout = (LinearLayout) itemView.findViewById(R.id.selectedHouseInfo);
        closePopup_btn = (ImageView) itemView.findViewById(R.id.closePopup_btn);
        return itemView;
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
