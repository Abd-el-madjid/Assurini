package com.example.assurini.Assurance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.assurini.Models.GrayCard;
import com.example.assurini.Models.Insurance;
import com.example.assurini.Models.InsuranceAgency;
import com.example.assurini.Models.InsuranceType;
import com.example.assurini.Models.Notification;
import com.example.assurini.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AssurancePlanFragment extends Fragment {

    private DatabaseReference databaseReference;
    private String agencyId, IdGrayCard;

    private static final String TAG = "AssurancePlanFragment";

    private TextView companyName;
    private TextView companyType;
    private TextView evaluation;
    private ImageView companyImage;
    private LinearLayout panel_raquest;
    private RelativeLayout no_car_panel;
    private TextView car_type;
    private TextView car_model;
    private TextView price;
    private TextView date_assurance;
    private Button request_button, hide_panel_request, show_panel_request;
    private CardView linear_layout_arrow ;
    private StorageReference storageReference;
    DatabaseReference notificationRef;
    private FirebaseUser currentUser;

private FirebaseAuth auth;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            agencyId = getArguments().getString("agencyId");
            IdGrayCard = getArguments().getString("IdGrayCard");
            Log.e("myapp", agencyId);

        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assurance_plan, container, false);

        companyName = view.findViewById(R.id.company_name);
        companyType = view.findViewById(R.id.company_anssurance_type);
        evaluation = view.findViewById(R.id.evaluation);
        companyImage = view.findViewById(R.id.image_company);
        panel_raquest = view.findViewById(R.id.car_ui_base_layout_content_container);
        car_type = view.findViewById(R.id.car_type);
        car_model = view.findViewById(R.id.car_model);
        price = view.findViewById(R.id.price_initial);
        date_assurance = view.findViewById(R.id.date_assurance);
        request_button = view.findViewById(R.id.request_button);
        hide_panel_request = view.findViewById(R.id.cancel_button);
        show_panel_request = view.findViewById(R.id.show_panel_request);
        linear_layout_arrow = view.findViewById(R.id.linear_layout_arrow);

        fetchCompanyDetail();
        fetchGrayCardDetail();

        show_panel_request.setOnClickListener(v -> panel_raquest.setVisibility(View.VISIBLE));
        hide_panel_request.setOnClickListener(v -> panel_raquest.setVisibility(View.GONE));

        linear_layout_arrow.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(AssurancePlanFragment.this).commit();

            CarListFragment carListFragment = new CarListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("agencyId", agencyId);
            carListFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, carListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        request_button.setOnClickListener(v -> {
            ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Please wait...");
            pd.show();
            panel_raquest.setVisibility(View.GONE);

            // Generate a unique key for the insurance object
            String insuranceId = databaseReference.child("Insurance").push().getKey();

            // Create an Insurance object
            Insurance insurance = new Insurance();
            insurance.setIdInsurance(insuranceId);
            insurance.setStartDate(Calendar.getInstance().getTime());

            // Calculate and set the end date (startDate + 1 year)
            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.add(Calendar.YEAR, 1);
            insurance.setEndDate(endDateCalendar.getTime());

            // Generate a unique police number
            insurance.setPoliceNumber(generatePoliceNumber());

            // Set the price from the TextView
            String priceText = price.getText().toString();
            priceText = priceText.replace("€", "").trim(); // Trim the string to remove any extra spaces
            double priceValue = new BigDecimal(priceText).doubleValue(); // Use BigDecimal for accurate double parsing
            insurance.setPrice(priceValue);
            insurance.setAgency(agencyId);
            insurance.setIdGrayCard(IdGrayCard);

            // Add the Insurance object to Firebase
            databaseReference.child("Insurance").child(insuranceId).setValue(insurance)
                    .addOnSuccessListener(aVoid -> {
                        // Update assure attribute to true in grayCards table
                        databaseReference.child("grayCards").child(IdGrayCard).child("assure").setValue(true)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Insurance request successful", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();

                                    // Generate and upload the PDF to Firebase Storage
                                    generateAndUploadPdf(insurance, insuranceId, pd);
                                    notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");
                                    currentUser = auth.getCurrentUser();
                                    String notificationId = notificationRef.push().getKey();
                                    String title = getContext().getString(R.string.notification_title_logout);
                                    String content = getContext().getString(R.string.notification_logout);
                                    String personId =currentUser.getUid();
                                    Date currentDate = new Date();

                                    // Create a Notification object
                                    Notification newNotification = new Notification(title, content, personId, currentDate);
                                    notificationRef.child(notificationId).setValue(newNotification);


                                    // Navigate to CompanyFragment
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().remove(AssurancePlanFragment.this).commit();

                                    CompanyFragment companyFragment = new CompanyFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("agencyId", agencyId);
                                    companyFragment.setArguments(bundle);

                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container, companyFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                })
                                .addOnFailureListener(e -> {
                                    pd.dismiss();
                                    Toast.makeText(getContext(), "Failed to update assure attribute", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Failed to update assure attribute", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Failed to request insurance", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to request insurance", e);
                    });
        });        return view;
    }

    private void fetchCompanyDetail() {
        databaseReference.child("InsuranceAgency").child(agencyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                InsuranceAgency agency = dataSnapshot.getValue(InsuranceAgency.class);
                if (agency != null) {
                    companyName.setText(agency.getName());
                    companyType.setText(agency.getInsuranceType());
                    evaluation.setText("25");
                    companyImage.setImageResource(R.drawable.company1);
                    fetchInsuranceTypeDetails(agency.getInsuranceType());
                    // Placeholder, replace with actual image if needed
                } else {
                    Toast.makeText(getContext(), "Agency details not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Agency details not found for ID: " + agencyId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load agency details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load agency details for ID: " + agencyId, databaseError.toException());
            }
        });
    }

    private void fetchGrayCardDetail() {
        databaseReference.child("grayCards").child(IdGrayCard).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GrayCard grayCard = dataSnapshot.getValue(GrayCard.class);
                if (grayCard != null) {
                    car_type.setText(grayCard.getType());
                    car_model.setText(grayCard.getMark());
                } else {
                    Toast.makeText(getContext(), "Gray card details not found", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Gray card details not found for ID: " + IdGrayCard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load gray card details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load gray card details for ID: " + IdGrayCard, databaseError.toException());
            }
        });

        // Set date of today + 1 year
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String nextYearDate = dateFormat.format(calendar.getTime());
        date_assurance.setText(nextYearDate);
    }
    private void fetchInsuranceTypeDetails(String insuranceTypeName) {
        DatabaseReference typeReference = FirebaseDatabase.getInstance().getReference("InsuranceType");
        typeReference.orderByChild("name").equalTo(insuranceTypeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InsuranceType insuranceType = snapshot.getValue(InsuranceType.class);
                    if (insuranceType != null) {
                        price.setText( insuranceType.getPrice() + "€");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to load insurance type details", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load insurance type details for name: " + insuranceTypeName, databaseError.toException());
            }
        });
    }
    private String generatePoliceNumber() {
        // Generate a unique police number using current date and a random number
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());

        int randomNum = (int) (Math.random() * 10000);
        String randomStr = String.format("%04d", randomNum);

        return "POL" + currentDate + randomStr;
    }



    private void generateAndUploadPdf(Insurance insurance, String insuranceId, ProgressDialog pd) {
        try {
            // Create a new PDF document
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            String path = getActivity().getExternalFilesDir(null).getAbsolutePath() + "/InsuranceFacture.pdf";
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Add title and content to the PDF
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 20, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14, com.itextpdf.text.Font.NORMAL);

            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Insurance Facture", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);

            document.add(new com.itextpdf.text.Paragraph("\n"));

            document.add(new com.itextpdf.text.Paragraph("Police Number: " + insurance.getPoliceNumber(), contentFont));
            document.add(new com.itextpdf.text.Paragraph("Price: " + insurance.getPrice() + "€", contentFont));
            document.add(new com.itextpdf.text.Paragraph("Start Date: " + insurance.getStartDate(), contentFont));
            document.add(new com.itextpdf.text.Paragraph("End Date: " + insurance.getEndDate(), contentFont));
            document.add(new com.itextpdf.text.Paragraph("What was done: Insurance requested", contentFont));

            document.close();

            // Upload the PDF to Firebase Storage
            Uri file = Uri.fromFile(new File(path));
            StorageReference pdfRef = storageReference.child("insuranceFactures/" + insuranceId + ".pdf");

            pdfRef.putFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Insurance Facture uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Get the download URL and update the insurancefacture attribute
                        pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            databaseReference.child("Insurance").child(insuranceId).child("insurancefacture").setValue(uri.toString());
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to get download URL", e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Failed to upload Insurance Facture", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to upload Insurance Facture", e);
                    });

        } catch (Exception e) {
            e.printStackTrace();
            pd.dismiss();
            Toast.makeText(getContext(), "Failed to generate PDF", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to generate PDF", e);
        }
    }

    private void openPdf(String path) {
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(uri, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Add this line to clear the activity stack
        startActivity(Intent.createChooser(pdfIntent, "Open with"));
    }
}
