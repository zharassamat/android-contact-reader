package kz.alashalemi.phonecontactsreadersample;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ContactItem> contactItems = getReadContacts();

        for(ContactItem item : contactItems) {
            Log.e("sj:", item.getDisplayName() + " ");
            for(PhoneContact phone : item.getArrayListPhone()) {
                Log.e("sj_phone:", phone.getPhone() + " ");
            }
            for(PostalAddress address : item.getArrayListAddress()) {
                Log.e("sj_address:", address.getCountry() + " " + address.getState() +  " " + address.getCity());
            }
            for(EmailContact email : item.getArrayListEmail()) {
                Log.e("sj_email", email.getEmail());
            }
        }
    }

    public ArrayList<ContactItem> getReadContacts() {
        ArrayList<ContactItem> contactList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor mainCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (mainCursor != null) {
            while (mainCursor.moveToNext()) {
                ContactItem contactItem = new ContactItem();
                String id = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String displayName = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
                Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);

                //ADD NAME AND CONTACT PHOTO DATA...
                contactItem.setDisplayName(displayName);
                contactItem.setPhotoUrl(displayPhotoUri.toString());

                if (Integer.parseInt(mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //ADD PHONE DATA...
                    ArrayList<PhoneContact> arrayListPhone = new ArrayList<>();
                    Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{
                            id
                    }, null);
                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            PhoneContact phoneContact = new PhoneContact();
                            String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneContact.setPhone(phone);
                            arrayListPhone.add(phoneContact);
                        }
                    }
                    if (phoneCursor != null) {
                        phoneCursor.close();
                    }
                    contactItem.setArrayListPhone(arrayListPhone);


                    //ADD E-MAIL DATA...
                    ArrayList<EmailContact> arrayListEmail = new ArrayList<>();
                    Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{
                            id
                    }, null);
                    if (emailCursor != null) {
                        while (emailCursor.moveToNext()) {
                            EmailContact emailContact = new EmailContact();
                            String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            emailContact.setEmail(email);
                            arrayListEmail.add(emailContact);
                        }
                    }
                    if (emailCursor != null) {
                        emailCursor.close();
                    }
                    contactItem.setArrayListEmail(arrayListEmail);

                    //ADD ADDRESS DATA...
                    ArrayList<PostalAddress> arrayListAddress = new ArrayList<>();
                    Cursor addrCursor = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?", new String[]{
                            id
                    }, null);
                    if (addrCursor != null) {
                        while (addrCursor.moveToNext()) {
                            PostalAddress postalAddress = new PostalAddress();
                            String city = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            String state = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                            String country = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                            postalAddress.setCity(city);
                            postalAddress.setState(state);
                            postalAddress.setCountry(country);
                            arrayListAddress.add(postalAddress);
                        }
                    }
                    if (addrCursor != null) {
                        addrCursor.close();
                    }
                    contactItem.setArrayListAddress(arrayListAddress);
                }
                contactList.add(contactItem);
            }
        }
        if (mainCursor != null) {
            mainCursor.close();
        }
        return contactList;
    }


}
