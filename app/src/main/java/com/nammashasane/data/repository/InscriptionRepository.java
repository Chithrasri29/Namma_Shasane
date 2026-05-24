package com.nammashasane.data.repository;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nammashasane.data.model.Inscription;
import com.nammashasane.data.model.PreservationAlert;
import com.nammashasane.utils.Constants;

import java.util.List;
import java.util.UUID;

public class InscriptionRepository {

    private static final String TAG = "InscriptionRepository";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public interface OnInscriptionsFetched {
        void onSuccess(List<Inscription> inscriptions);
        void onError(String error);
    }

    public interface OnOperationComplete {
        void onSuccess(String id);
        void onError(String error);
    }

    public InscriptionRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void getRecentInscriptions(OnInscriptionsFetched callback) {
        db.collection(Constants.COLLECTION_INSCRIPTIONS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Inscription> list = querySnapshot.toObjects(Inscription.class);
                    // Set document IDs
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setId(querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch inscriptions", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getInscriptionById(String id, OnInscriptionsFetched callback) {
        db.collection(Constants.COLLECTION_INSCRIPTIONS)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Inscription inscription = documentSnapshot.toObject(Inscription.class);
                    if (inscription != null) {
                        inscription.setId(documentSnapshot.getId());
                        callback.onSuccess(List.of(inscription));
                    } else {
                        callback.onError("Inscription not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void uploadInscriptionWithPhoto(Uri photoUri, Inscription inscription,
                                           OnOperationComplete callback) {
        if (photoUri != null) {
            String filename = "inscriptions/" + UUID.randomUUID() + ".jpg";
            StorageReference ref = storage.getReference().child(filename);
            ref.putFile(photoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            inscription.setPhotoUrl(uri.toString());
                            saveInscription(inscription, callback);
                        });
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            saveInscription(inscription, callback);
        }
    }

    private void saveInscription(Inscription inscription, OnOperationComplete callback) {
        db.collection(Constants.COLLECTION_INSCRIPTIONS)
                .add(inscription)
                .addOnSuccessListener(ref -> callback.onSuccess(ref.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void uploadAlertWithPhoto(Uri photoUri, PreservationAlert alert,
                                     OnOperationComplete callback) {
        if (photoUri != null) {
            String filename = "alerts/" + UUID.randomUUID() + ".jpg";
            StorageReference ref = storage.getReference().child(filename);
            ref.putFile(photoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            alert.setPhotoUrl(uri.toString());
                            saveAlert(alert, callback);
                        });
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            saveAlert(alert, callback);
        }
    }

    private void saveAlert(PreservationAlert alert, OnOperationComplete callback) {
        db.collection(Constants.COLLECTION_ALERTS)
                .add(alert)
                .addOnSuccessListener(ref -> callback.onSuccess(ref.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateTranslation(String inscriptionId, String translation,
                                  String dynasty, String period, String giftLaw) {
        db.collection(Constants.COLLECTION_INSCRIPTIONS)
                .document(inscriptionId)
                .update(
                        "translationKannada", translation,
                        "dynasty", dynasty,
                        "period", period,
                        "giftLaw", giftLaw
                )
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update translation", e));
    }
}
