package com.vectorcoder.androidecommerce.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.*;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.cardform.utils.CardType;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Token;

import com.vectorcoder.androidecommerce.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.vectorcoder.androidecommerce.activities.MainActivity;
import com.vectorcoder.androidecommerce.adapters.DemoCouponsListAdapter;
import com.vectorcoder.androidecommerce.adapters.PaymentMethodAdapter;
import com.vectorcoder.androidecommerce.app.App;
import com.vectorcoder.androidecommerce.constant.ConstantValues;
import com.vectorcoder.androidecommerce.customs.DialogLoader;
import com.vectorcoder.androidecommerce.databases.User_Cart_DB;
import com.vectorcoder.androidecommerce.databases.User_Info_DB;
import com.vectorcoder.androidecommerce.adapters.CouponsAdapter;
import com.vectorcoder.androidecommerce.adapters.CheckoutItemsAdapter;
import com.vectorcoder.androidecommerce.models.address_model.AddressDetails;
import com.vectorcoder.androidecommerce.models.cart_model.CartProduct;
import com.vectorcoder.androidecommerce.models.cart_model.CartProductAttributes;
import com.vectorcoder.androidecommerce.models.coupons_model.CouponsData;
import com.vectorcoder.androidecommerce.models.coupons_model.CouponsInfo;
import com.vectorcoder.androidecommerce.models.order_model.OrderData;
import com.vectorcoder.androidecommerce.models.payment_model.GetBrainTreeToken;
import com.vectorcoder.androidecommerce.models.payment_model.HyperPayToken;
import com.vectorcoder.androidecommerce.models.payment_model.PaymentMethodsData;
import com.vectorcoder.androidecommerce.models.payment_model.PaymentMethodsInfo;
import com.vectorcoder.androidecommerce.models.order_model.PostOrder;
import com.vectorcoder.androidecommerce.models.order_model.PostProductsAttributes;
import com.vectorcoder.androidecommerce.models.order_model.PostProducts;
import com.vectorcoder.androidecommerce.models.product_model.Option;
import com.vectorcoder.androidecommerce.models.product_model.Value;
import com.vectorcoder.androidecommerce.models.shipping_model.ShippingService;
import com.vectorcoder.androidecommerce.models.user_model.UserDetails;
import com.vectorcoder.androidecommerce.customs.DividerItemDecoration;
import com.vectorcoder.androidecommerce.network.APIClient;
import com.vectorcoder.androidecommerce.utils.NotificationHelper;
import com.vectorcoder.androidecommerce.utils.ValidateInputs;
import com.vectorcoder.androidecommerce.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import retrofit2.Callback;
import retrofit2.Call;


public class Checkout extends Fragment {

    View rootView;
    AlertDialog demoCouponsDialog;
    boolean disableOtherCoupons = false;
    
    String tax;
    String braintreeToken;
    String hyperPayToken;
    String selectedPaymentMethod;
    String paymentNonceToken = "";
    double checkoutSubtotal, checkoutTax, checkoutShipping, checkoutShippingCost, checkoutDiscount, checkoutTotal = 0;
    
    WebView checkout_webView;
    
    Button checkout_instamojo_btn;
    Button checkout_paypal_btn;
    CardView card_details_layout;
    ProgressDialog progressDialog;
    NestedScrollView scroll_container;
    RecyclerView checkout_items_recycler;
    RecyclerView checkout_coupons_recycler;
    Button checkout_coupon_btn, checkout_order_btn, checkout_cancel_btn;
    ImageButton edit_billing_Btn, edit_shipping_Btn, edit_shipping_method_Btn;
    EditText payment_name, payment_email, payment_phone,checkout_coupon_code, checkout_comments, checkout_card_number, checkout_card_cvv, checkout_card_expiry;
    TextView checkout_subtotal, checkout_tax, checkout_shipping, checkout_discount, checkout_total, demo_coupons_text;
    TextView billing_name, billing_street, billing_address, shipping_name, shipping_street, shipping_address, shipping_method, payment_method;
    
    List<CouponsInfo> couponsList;
    List<CartProduct> checkoutItemsList;
    List<PaymentMethodsInfo> paymentMethodsList;
    
    UserDetails userInfo;
    DialogLoader dialogLoader;
    AddressDetails billingAddress;
    AddressDetails shippingAddress;
    CouponsAdapter couponsAdapter;
    ShippingService shippingMethod;
    CheckoutItemsAdapter checkoutItemsAdapter;
    
    User_Cart_DB user_cart_db = new User_Cart_DB();
    User_Info_DB user_info_db = new User_Info_DB();
    
    
    
    CardView instamojo_details_layout;
    
    CardBuilder braintreeCard;
    BraintreeFragment braintreeFragment;
    com.stripe.android.model.Card stripeCard;

    private String PAYMENT_CURRENCY = "USD";
    private String STRIPE_PUBLISHABLE_KEY = "";
    private String PAYPAL_PUBLISHABLE_KEY = "";
    private String INSTAMOJO_PUBLISHABLE_KEY = "";
    private String PAYMENT_ENVIRONMENT = "Test";
    
    private String SUCCESS = "success";
    
    private static PayPalConfiguration payPalConfiguration;
    private static final int SIMPLE_PAYPAL_REQUEST_CODE = 123;
    
    
    CardType cardType;
    SupportedCardTypesView braintreeSupportedCards;

    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.MAESTRO,
                                                             CardType.UNIONPAY, CardType.AMEX};
    


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.checkout, container, false);
        
        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.checkout));

        // Get selectedShippingMethod, billingAddress and shippingAddress from ApplicationContext
        tax = ((App) getContext().getApplicationContext()).getTax();
        shippingMethod = ((App) getContext().getApplicationContext()).getShippingService();
        billingAddress = ((App) getContext().getApplicationContext()).getBillingAddress();
        shippingAddress = ((App) getContext().getApplicationContext()).getShippingAddress();

        // Get userInfo from Local Databases User_Info_DB
        userInfo = user_info_db.getUserData(getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", null));
        
        

        // Binding Layout Views
        checkout_order_btn = (Button) rootView.findViewById(R.id.checkout_order_btn);
        checkout_cancel_btn = (Button) rootView.findViewById(R.id.checkout_cancel_btn);
        checkout_coupon_btn = (Button) rootView.findViewById(R.id.checkout_coupon_btn);
        edit_billing_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_billing);
        edit_shipping_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_shipping);
        edit_shipping_method_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_shipping_method);
        shipping_method = (TextView) rootView.findViewById(R.id.shipping_method);
        payment_method = (TextView) rootView.findViewById(R.id.payment_method);
        checkout_subtotal = (TextView) rootView.findViewById(R.id.checkout_subtotal);
        checkout_tax = (TextView) rootView.findViewById(R.id.checkout_tax);
        checkout_shipping = (TextView) rootView.findViewById(R.id.checkout_shipping);
        checkout_discount = (TextView) rootView.findViewById(R.id.checkout_discount);
        checkout_total = (TextView) rootView.findViewById(R.id.checkout_total);
        shipping_name = (TextView) rootView.findViewById(R.id.shipping_name);
        shipping_street = (TextView) rootView.findViewById(R.id.shipping_street);
        shipping_address = (TextView) rootView.findViewById(R.id.shipping_address);
        billing_name = (TextView) rootView.findViewById(R.id.billing_name);
        billing_street = (TextView) rootView.findViewById(R.id.billing_street);
        billing_address = (TextView) rootView.findViewById(R.id.billing_address);
        demo_coupons_text = (TextView) rootView.findViewById(R.id.demo_coupons_text);
        checkout_coupon_code = (EditText) rootView.findViewById(R.id.checkout_coupon_code);
        checkout_comments = (EditText) rootView.findViewById(R.id.checkout_comments);
        payment_name = (EditText) rootView.findViewById(R.id.payment_name);
        payment_email = (EditText) rootView.findViewById(R.id.payment_email);
        payment_phone = (EditText) rootView.findViewById(R.id.payment_phone);
        checkout_items_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_items_recycler);
        checkout_coupons_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_coupons_recycler);
        checkout_webView = (WebView)rootView.findViewById(R.id.checkout_webView);
        
        card_details_layout = (CardView) rootView.findViewById(R.id.card_details_layout);
        instamojo_details_layout = (CardView) rootView.findViewById(R.id.instamojo_details_layout);
        checkout_paypal_btn = (Button) rootView.findViewById(R.id.checkout_paypal_btn);
        checkout_instamojo_btn = (Button) rootView.findViewById(R.id.checkout_instamojo_btn);
        checkout_card_number = (EditText) rootView.findViewById(R.id.checkout_card_number);
        checkout_card_cvv = (EditText) rootView.findViewById(R.id.checkout_card_cvv);
        checkout_card_expiry = (EditText) rootView.findViewById(R.id.checkout_card_expiry);
        scroll_container = (NestedScrollView) rootView.findViewById(R.id.scroll_container);
        braintreeSupportedCards = (SupportedCardTypesView) rootView.findViewById(R.id.supported_card_types);


        braintreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
    
        checkout_order_btn.setEnabled(false);
        card_details_layout.setVisibility(View.GONE);
        checkout_paypal_btn.setVisibility(View.GONE);
        instamojo_details_layout.setVisibility(View.GONE);


        checkout_items_recycler.setNestedScrollingEnabled(false);
        checkout_coupons_recycler.setNestedScrollingEnabled(false);

        checkout_card_expiry.setKeyListener(null);

        
        dialogLoader = new DialogLoader(getContext());
    
    
        couponsList = new ArrayList<>();
        checkoutItemsList = new ArrayList<>();
        paymentMethodsList = new ArrayList<>();
        
        // Get checkoutItems from Local Databases User_Cart_DB
        checkoutItemsList = user_cart_db.getCartItems();

        
        // Request Payment Methods
        RequestPaymentMethods();


        // Initialize the CheckoutItemsAdapter for RecyclerView
        checkoutItemsAdapter = new CheckoutItemsAdapter(getContext(), checkoutItemsList);

        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        checkout_items_recycler.setAdapter(checkoutItemsAdapter);
        checkout_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        checkout_items_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    
    
    
        // Initialize the CouponsAdapter for RecyclerView
        couponsAdapter = new CouponsAdapter(getContext(), couponsList, true, Checkout.this);
    
        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        checkout_coupons_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        checkout_coupons_recycler.setAdapter(couponsAdapter);

        couponsAdapter.notifyDataSetChanged();



        checkoutTax = Double.parseDouble(tax);
        shipping_method.setText(shippingMethod.getName());
        checkoutShipping = checkoutShippingCost = Double.parseDouble(shippingMethod.getRate());
    
        // Set Billing Details
        shipping_name.setText(shippingAddress.getFirstname()+" "+shippingAddress.getLastname());
        shipping_address.setText(shippingAddress.getZoneName()+", "+shippingAddress.getCountryName());
        shipping_street.setText(shippingAddress.getStreet());

        // Set Billing Details
        billing_name.setText(billingAddress.getFirstname()+" "+billingAddress.getLastname());
        billing_address.setText(billingAddress.getZoneName()+", "+billingAddress.getCountryName());
        billing_street.setText(billingAddress.getStreet());


        // Set Checkout Total
        setCheckoutTotal();


        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.processing));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    
    
    
        // Handle the Click event of edit_payment_method_Btn
        payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList);
    
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
    
                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
    
                dialog_button.setVisibility(View.GONE);
    
                dialog_title.setText(getString(R.string.payment_method));
                dialog_list.setAdapter(paymentMethodAdapter);
    
    
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
    
    
    
                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
                        PaymentMethodsInfo userSelectedPaymentMethod = paymentMethodAdapter.getItem(position);
    
                        payment_method.setText(userSelectedPaymentMethod.getName());
                        selectedPaymentMethod = userSelectedPaymentMethod.getMethod();
                        
                        checkout_order_btn.setEnabled(true);
                        checkout_order_btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentGreen));
    
                        
                        // Check the selected Payment Method
                        switch (userSelectedPaymentMethod.getMethod()) {
        
                            // Change the Visibility of some Views based on selected Payment Method
                            case "cod":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                break;
        
                            case "paypal":
                                checkout_paypal_btn.setVisibility(View.VISIBLE);
                                card_details_layout.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                break;
        
                            case "stripe":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.VISIBLE);
            
                                checkout_card_number.setText("4242424242424242");
                                checkout_card_cvv.setText("123");
                                checkout_card_expiry.setText("12/2018");
                                break;
                            case "hyperpay":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.VISIBLE);
        
                                checkout_card_number.setText("4242424242424242");
                                checkout_card_cvv.setText("123");
                                checkout_card_expiry.setText("12/2018");
                                break;
                            case "braintree_card":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.VISIBLE);
            
                                checkout_card_number.setText("5555555555554444");
                                checkout_card_cvv.setText("123");
                                checkout_card_expiry.setText("12/2018");
                                break;
        
                            case "braintree_paypal":
                                checkout_paypal_btn.setVisibility(View.VISIBLE);
                                card_details_layout.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                break;
                                
                            case "instamojo":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.VISIBLE);
                                break;
                            default:
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                instamojo_details_layout.setVisibility(View.GONE);
                                break;
                        }
    
                        scroll_container.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_container.fullScroll(scroll_container.FOCUS_DOWN);
                            }
                        });
            
            
                        alertDialog.dismiss();
            
                    }
                });
            
                /*final ArrayAdapter paymentAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                paymentAdapter.addAll(paymentMethodsList);
            
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
            
                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
            
                dialog_button.setVisibility(View.GONE);
            
                dialog_title.setText(getString(R.string.payment_method));
                dialog_list.setAdapter(paymentAdapter);
            
            
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            
            
                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    
                        String userSelectedPaymentMethod = paymentAdapter.getItem(position).toString();
                    
                        payment_method.setText(userSelectedPaymentMethod);
                        checkout_order_btn.setEnabled(true);
                        checkout_order_btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentGreen));
                    
                        // Check the selected Payment Method
                        switch (userSelectedPaymentMethod) {
                        
                            // Change the Visibility of some Views based on selected Payment Method
                            case "Cash On Delivery":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                selectedPaymentMethod = "cash_on_delivery";
                                break;
                                
                            case "PayPal":
                                checkout_paypal_btn.setVisibility(View.VISIBLE);
                                card_details_layout.setVisibility(View.GONE);
                                selectedPaymentMethod = "simplePaypal";
                                break;
                                
                            case "Stripe Credit Card":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.VISIBLE);
                                selectedPaymentMethod = "stripe";
                            
                                checkout_card_number.setText("4242424242424242");
                                checkout_card_cvv.setText("123");
                                checkout_card_expiry.setText("12/2018");
                                break;
                                
                            case "Braintree Credit Card":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.VISIBLE);
                                selectedPaymentMethod = "card_payment";
                            
                                checkout_card_number.setText("5555555555554444");
                                checkout_card_cvv.setText("123");
                                checkout_card_expiry.setText("12/2018");
                                break;
                                
                            case "Braintree PayPal":
                                checkout_paypal_btn.setVisibility(View.VISIBLE);
                                card_details_layout.setVisibility(View.GONE);
                                selectedPaymentMethod = "paypal";
                                break;
                                
                            default:
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                selectedPaymentMethod = "cash_on_delivery";
                                break;
                        }
                    
                        scroll_container.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_container.fullScroll(scroll_container.FOCUS_DOWN);
//                                scroll_container.scrollTo(0, scroll_container.getBottom());
                            }
                        });
                    
                        alertDialog.dismiss();
                    }
                });*/
            }
        });
        
        
        // Integrate SupportedCardTypes with TextChangedListener of checkout_card_number
        checkout_card_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(checkout_card_number.getText().toString().trim())) {
                    CardType type = CardType.forCardNumber(checkout_card_number.getText().toString());
                    if (cardType != type) {
                        cardType = type;
                    
                        InputFilter[] filters = { new InputFilter.LengthFilter(cardType.getMaxCardLength()) };
                        checkout_card_number.setFilters(filters);
                        checkout_card_number.invalidate();
                    
                        braintreeSupportedCards.setSelected(cardType);
                    }
                } else {
                    braintreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        
        // Handle Touch event of input_dob EditText
        checkout_card_expiry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get Calendar instance
                    final Calendar calendar = Calendar.getInstance();

                    // Initialize DateSetListener of DatePickerDialog
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            // Set the selected Date Info to Calendar instance
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);

                            // Set Date Format
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.US);

                            // Set Date in input_dob EditText
                            checkout_card_expiry.setText(dateFormat.format(calendar.getTime()));
                        }
                    };


                    // Initialize DatePickerDialog
                    DatePickerDialog datePicker = new DatePickerDialog
                            (
                                    getContext(),
                                    date,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );

                    // Show datePicker Dialog
                    datePicker.show();
                }

                return false;
            }
        });
    
    
    
        // Handle the Click event of checkout_instamojo_btn Button
        checkout_instamojo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
                if (validatePaymentInfo()) {
                
                    JSONObject pay = new JSONObject();
                    Activity activity = getActivity();
                    InstamojoPay instamojoPay = new InstamojoPay();
                    IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
                    getActivity().registerReceiver(instamojoPay, filter);
                
                    try {
                        pay.put("id", INSTAMOJO_PUBLISHABLE_KEY);
                        pay.put("name", payment_name.getText().toString());
                        pay.put("email", payment_email.getText().toString());
                        pay.put("phone", payment_phone.getText().toString());
                        pay.put("amount", String.valueOf(checkoutTotal));
                        pay.put("currency", PAYMENT_CURRENCY);
                        pay.put("purpose", "shopping");
                        pay.put("send_sms", false);
                        pay.put("send_email", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                
                    InstapayListener listener = new InstapayListener() {
                        @Override
                        public void onSuccess(String response) {
                        
                            String[] responseArray = response.split(":");
                            String orderID = responseArray[1].substring(responseArray[1].indexOf("=")+1);
                            String paymentId = responseArray[3].substring(responseArray[3].indexOf("=")+1);
                            String token = responseArray[4].substring(responseArray[4].indexOf("=")+1);
                        
                            Log.i("VC_Shop", "[Instamojo] > response="+response);
                        
                            selectedPaymentMethod = "instamojo";
                            paymentNonceToken = paymentId;
                        }
                    
                        @Override
                        public void onFailure(int code, String reason) {
                            Toast.makeText(getActivity(), "Error: " + reason, Toast.LENGTH_LONG).show();
                            Log.i("VC_Shop", "[Instamojo] > reason="+reason);
                        }
                    };
                
                    instamojoPay.start(activity, pay, listener);
                }
            }
        });
    
    
        // Handle the Click event of checkout_paypal_btn Button
        checkout_paypal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (selectedPaymentMethod.equalsIgnoreCase("braintree_paypal")) {
                    // Process Payment using Braintree PayPal
                    PayPal.authorizeAccount(braintreeFragment);
                }
                else if (selectedPaymentMethod.equalsIgnoreCase("paypal")) {
                    // Process Payment using PayPal
                    PayPalPayment payment = new PayPalPayment
                        (
                            new BigDecimal(String.valueOf(checkoutTotal)),
                            PAYMENT_CURRENCY,
                            ConstantValues.APP_HEADER,
                            PayPalPayment.PAYMENT_INTENT_SALE
                        );
    
                    Intent intent = new Intent(getContext(), PaymentActivity.class);
    
                    // send the same configuration for restart resiliency
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
    
                    startActivityForResult(intent, SIMPLE_PAYPAL_REQUEST_CODE);
                }
            }
        });



        // Handle the Click event of edit_billing_Btn Button
        edit_billing_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to Billing_Address Fragment to Edit BillingAddress
                Fragment fragment = new Billing_Address();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });


        // Handle the Click event of edit_shipping_Btn Button
        edit_shipping_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to Shipping_Address Fragment to Edit ShippingAddress
                Fragment fragment = new Shipping_Address();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });


        // Handle the Click event of edit_shipping_method_Btn Button
        edit_shipping_method_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to Shipping_Methods Fragment to Edit ShippingMethod
                Fragment fragment = new Shipping_Methods();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });
    
        

        if (!ConstantValues.IS_CLIENT_ACTIVE) {
            setupDemoCoupons();
        }
        else {
            demo_coupons_text.setVisibility(View.GONE);
        }
        

        // Handle the Click event of checkout_coupon_btn Button
        checkout_coupon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(checkout_coupon_code.getText().toString())) {
                    GetCouponInfo(checkout_coupon_code.getText().toString());
                    dialogLoader.showProgressDialog();
                }
            }
        });


        // Handle the Click event of checkout_cancel_btn Button
        checkout_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel the Order and Navigate back to My_Cart Fragment
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });


        // Handle the Click event of checkout_order_btn Button
        checkout_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                // Check if the selectedPaymentMethod is not "Cash_on_Delivery"
                if (!selectedPaymentMethod.equalsIgnoreCase("cod")) {

                    // Check if the selectedPaymentMethod is not "PayPal" or "Braintree PayPal"
                    if (!selectedPaymentMethod.equalsIgnoreCase("braintree_paypal")
                        &&  !selectedPaymentMethod.equalsIgnoreCase("paypal"))
                    {

                        if (validatePaymentCard()) {
                            // Setup Payment Method
                            validateSelectedPaymentMethod();
                            progressDialog.show();

                            // Delay of 2 seconds
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!"".equalsIgnoreCase(paymentNonceToken)) {
                                        // Proceed Order
                                        proceedOrder();
                                    } else {
                                        progressDialog.dismiss();
                                        Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }, 2000);
                        }

                    }
                    else {
                        // Setup Payment Method
                        validateSelectedPaymentMethod();
                        progressDialog.show();

                        // Delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!"".equalsIgnoreCase(paymentNonceToken)) {
                                    // Proceed Order
                                    proceedOrder();
                                }
                                else {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }, 2000);
                    }
                }
                else {
                    // Proceed Order
                    proceedOrder();
                    progressDialog.show();
                }
            }
        });


        return rootView;
    }
    
    
    
    //*********** Called when the fragment is no longer in use ********//
    
    @Override
    public void onDestroy() {
        getContext().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }
    
    
    
    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            
            if (requestCode == BraintreeRequestCodes.PAYPAL) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                
                // use the result to update your UI and send the payment method nonce to your server
                if (!TextUtils.isEmpty(result.getPaymentMethodNonce().getNonce())) {
                    selectedPaymentMethod = "braintree_paypal";
                    paymentNonceToken = result.getPaymentMethodNonce().getNonce();
                }
                
            }
            else if (requestCode == SIMPLE_PAYPAL_REQUEST_CODE) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                
                if (confirm != null) {
                    selectedPaymentMethod = "paypal";
                    paymentNonceToken = confirm.getProofOfPayment().getPaymentId();
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paypal", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paypal", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
    


    //*********** Validate Payment method Details according to the selectedPaymentMethod ********//

    private void validateSelectedPaymentMethod() {

        // Check if the selectedPaymentMethod is Braintree's card_payment
        if (selectedPaymentMethod.equalsIgnoreCase("braintree_card")) {

            // Initialize BraintreeCard
            braintreeCard = new CardBuilder()
                    .cardNumber(checkout_card_number.getText().toString().trim())
                    .expirationDate(checkout_card_expiry.getText().toString().trim())
                    .cvv(checkout_card_cvv.getText().toString().trim());
    
            try {
                braintreeFragment = BraintreeFragment.newInstance(getActivity(), braintreeToken);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }

            // Tokenize BraintreeCard
            Card.tokenize(braintreeFragment, braintreeCard);


            // Add PaymentMethodNonceCreatedListener to BraintreeFragment
            braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

                    // Get Payment Nonce
                    paymentNonceToken = paymentMethodNonce.getNonce();
                }
            });


            // Add BraintreeErrorListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {

                    // Check if there was a Validation Error of provided Data
                    if (error instanceof ErrorWithResponse) {
                        ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;

                        BraintreeError cardNumberError = errorWithResponse.errorFor("number");
                        BraintreeError cardCVVErrors = errorWithResponse.errorFor("creditCard");
                        BraintreeError expirationMonthError = errorWithResponse.errorFor("expirationMonth");
                        BraintreeError expirationYearError = errorWithResponse.errorFor("expirationYear");

                        // Check if there is an Issue with the Credit Card
                        if (cardNumberError != null) {
                            checkout_card_number.setError(cardNumberError.getMessage());
                        }
                        else if (expirationMonthError != null) {
                            checkout_card_expiry.setError(expirationMonthError.getMessage());
                        }
                        else if (expirationYearError != null) {
                            checkout_card_expiry.setError(expirationYearError.getMessage());
                        }
                        else if (cardCVVErrors != null) {
                            checkout_card_cvv.setError(cardCVVErrors.getMessage());
                        }
                        else {
                            Toast.makeText(getContext(), errorWithResponse.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            // Add ConfigurationListener to BraintreeFragment
            braintreeFragment.addListener(new ConfigurationListener() {
                @Override
                public void onConfigurationFetched(Configuration configuration) {}
            });

            // Add BraintreeCancelListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {}
            });


        }
        // Check if the selectedPaymentMethod is Stripe's card_payment
        else if (selectedPaymentMethod.equalsIgnoreCase("stripe")) {

            String[] expiryDate = checkout_card_expiry.getText().toString().split("/");

            // Initialize StripeCard
            stripeCard = new com.stripe.android.model.Card
                    (
                            checkout_card_number.getText().toString().trim(),
                            Integer.valueOf(expiryDate[0]),
                            Integer.valueOf(expiryDate[1]),
                            checkout_card_cvv.getText().toString().trim()
                    );

            Stripe stripe = null;

            if (stripeCard.validateCard()) {
                try {
                    // Initialize Stripe with Stripe API Key
                    stripe = new Stripe(STRIPE_PUBLISHABLE_KEY);

                    // Create Token of the StripeCard
                    stripe.createToken(
                            stripeCard,
                            new TokenCallback() {

                                // Handle onSuccess Callback
                                public void onSuccess(Token token) {

                                    // Get Payment Nonce
                                    paymentNonceToken = token.getId();
                                }

                                // Handle onError Callback
                                public void onError(Exception error) {

                                    // Check if there is an Issue with the Credit Card
                                    if (!stripeCard.validateNumber()) {
                                        checkout_card_number.setError(getString(R.string.invalid_credit_card));
                                    } else if (!stripeCard.validateExpiryDate()) {
                                        checkout_card_expiry.setError(getString(R.string.expired_card));
                                    } else if (!stripeCard.validateCVC()) {
                                        checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
                                    } else {
                                        checkout_card_number.setError(getString(R.string.invalid_credit_card));
                                    }
                                }
                            }
                    );
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }

            } else if (!stripeCard.validateNumber()) {
                checkout_card_number.setError(getString(R.string.invalid_credit_card));
            } else if (!stripeCard.validateExpiryDate()) {
                checkout_card_expiry.setError(getString(R.string.expired_card));
            } else if (!stripeCard.validateCVC()) {
                checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
            } else {
                checkout_card_number.setError(getString(R.string.invalid_credit_card));
            }


        }
        // Check if the selectedPaymentMethod is Braintree's PayPal
        else if (selectedPaymentMethod.equalsIgnoreCase("braintree_paypal")) {

            // Add PaymentMethodNonceCreatedListener to BraintreeFragment
            braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

                    // Get Payment Nonce
                    paymentNonceToken = paymentMethodNonce.getNonce();
                    selectedPaymentMethod = "braintree_paypal";
                }
            });
    
            // Add BraintreeErrorListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add BraintreeCancelListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {}
            });

        }
        // Check if the selectedPaymentMethod is Braintree's PayPal
        else if (selectedPaymentMethod.equalsIgnoreCase("paypal")) {
    
            try {
                braintreeFragment = BraintreeFragment.newInstance(getActivity(), braintreeToken);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
            // Add PaymentMethodNonceCreatedListener to BraintreeFragment
            braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
            
                    // Get Payment Nonce
                    paymentNonceToken = paymentMethodNonce.getNonce();
                    selectedPaymentMethod = "paypal";
                }
            });
    
            // Add BraintreeErrorListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    
            // Add BraintreeCancelListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {}
            });
    
        }
        else {
            return;
        }
    }


    //*********** Returns Final Price of User's Cart ********//

    private double getProductsSubTotal() {

        double finalPrice = 0;

        for (int i=0;  i<checkoutItemsList.size();  i++) {
            // Add the Price of each Cart Product to finalPrice
            finalPrice += Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }

        return finalPrice;
    }



    //*********** Set Checkout's Subtotal, Tax, ShippingCost, Discount and Total Prices ********//

    private void setCheckoutTotal() {

        
        // Get Cart Total
        checkoutSubtotal = getProductsSubTotal();
        
        // Calculate Checkout Total
        checkoutTotal = checkoutSubtotal + checkoutTax + checkoutShipping - checkoutDiscount;
    
        // Set Checkout Details
        checkout_tax.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTax));
        checkout_shipping.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutShipping));
        checkout_discount.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutDiscount));
        
        checkout_subtotal.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutSubtotal));
        checkout_total.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTotal));

    }



    //*********** Set Order Details to proceed Checkout ********//

    private void proceedOrder() {
        // Formate to minimum two digit end
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        PostOrder orderDetails = new PostOrder();
        List<PostProducts> orderProductList = new ArrayList<>();
    
        for (int i=0;  i<checkoutItemsList.size();  i++) {
        
            PostProducts orderProduct = new PostProducts();
           
            String str = ""+checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs();
            // Get current Product Details
            orderProduct.setProductsId(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId());
            orderProduct.setProductsName(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsName());
            orderProduct.setModel(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsModel());
            orderProduct.setImage(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsImage());
            orderProduct.setWeight(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsWeight());
            orderProduct.setUnit(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsWeightUnit());
            orderProduct.setManufacture(checkoutItemsList.get(i).getCustomersBasketProduct().getManufacturersName());
            orderProduct.setCategoriesId(""+checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());
            orderProduct.setCategoriesName(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryNames());
            orderProduct.setPrice(formatter.format(Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsPrice())));
            orderProduct.setFinalPrice(formatter.format(Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice())));
            orderProduct.setSubtotal(formatter.format(Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice())));
            orderProduct.setTotal(formatter.format(Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice())));
            orderProduct.setCustomersBasketQuantity(checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
        
            orderProduct.setOnSale(checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1"));
    
            
            List<PostProductsAttributes> productAttributes = new ArrayList<>();
            
            for (int j=0;  j<checkoutItemsList.get(i).getCustomersBasketProductAttributes().size();  j++) {
                CartProductAttributes cartProductAttributes = checkoutItemsList.get(i).getCustomersBasketProductAttributes().get(j);
                Option attributeOption = cartProductAttributes.getOption();
                Value attributeValue = cartProductAttributes.getValues().get(0);
    
                PostProductsAttributes attribute = new PostProductsAttributes();
                attribute.setProductsOptionsId(String.valueOf(attributeOption.getId()));
                attribute.setProductsOptions(attributeOption.getName());
                attribute.setProductsOptionsValuesId(String.valueOf(attributeValue.getId()));
                attribute.setProductsOptionsValues(attributeValue.getValue());
                attribute.setOptionsValuesPrice(attributeValue.getPrice());
                attribute.setPricePrefix(attributeValue.getPricePrefix());
                attribute.setAttributeName(attributeValue.getValue()+" "+attributeValue.getPricePrefix()+attributeValue.getPrice());
    
                productAttributes.add(attribute);
            }
    
            orderProduct.setAttributes(productAttributes);
        
        
            // Add current Product to orderProductList
            orderProductList.add(orderProduct);
        }
    
    
        // Set Customer Info
        orderDetails.setCustomersId(Integer.parseInt(userInfo.getCustomersId()));
        orderDetails.setCustomersName(userInfo.getCustomersFirstname());
        orderDetails.setCustomersTelephone(userInfo.getCustomersTelephone());
        orderDetails.setCustomersEmailAddress(userInfo.getCustomersEmailAddress());
    
        // Set Shipping  Info
        orderDetails.setDeliveryFirstname(shippingAddress.getFirstname());
        orderDetails.setDeliveryLastname(shippingAddress.getLastname());
        orderDetails.setDeliveryStreetAddress(shippingAddress.getStreet());
        orderDetails.setDeliveryPostcode(shippingAddress.getPostcode());
        orderDetails.setDeliverySuburb(shippingAddress.getSuburb());
        orderDetails.setDeliveryCity(shippingAddress.getCity());
        orderDetails.setDeliveryZone(shippingAddress.getZoneName());
        orderDetails.setDeliveryState(shippingAddress.getZoneName());
        orderDetails.setDeliverySuburb(shippingAddress.getZoneName());
        orderDetails.setDeliveryCountry(shippingAddress.getCountryName());
        orderDetails.setDeliveryZoneId(String.valueOf(shippingAddress.getZoneId()));
        orderDetails.setDeliveryCountryId(String.valueOf(shippingAddress.getCountriesId()));
        orderDetails.setDeliveryPhone(String.valueOf(shippingAddress.getDeliveryPhone()));
        
    
        // Set Billing Info
        orderDetails.setBillingFirstname(billingAddress.getFirstname());
        orderDetails.setBillingLastname(billingAddress.getLastname());
        orderDetails.setBillingStreetAddress(billingAddress.getStreet());
        orderDetails.setBillingPostcode(billingAddress.getPostcode());
        orderDetails.setBillingSuburb(billingAddress.getSuburb());
        orderDetails.setBillingCity(billingAddress.getCity());
        orderDetails.setBillingZone(billingAddress.getZoneName());
        orderDetails.setBillingState(billingAddress.getZoneName());
        orderDetails.setBillingSuburb(billingAddress.getZoneName());
        orderDetails.setBillingCountry(billingAddress.getCountryName());
        orderDetails.setBillingZoneId(String.valueOf(billingAddress.getZoneId()));
        orderDetails.setBillingCountryId(String.valueOf(billingAddress.getCountriesId()));
        orderDetails.setBillingPhone(String.valueOf(billingAddress.getDeliveryPhone()));
    
        orderDetails.setLanguage_id(ConstantValues.LANGUAGE_ID);
        
        orderDetails.setTaxZoneId(shippingAddress.getZoneId());
        orderDetails.setTotalTax(Double.parseDouble(formatter.format(checkoutTotal)));
        orderDetails.setShippingCost(Double.parseDouble(formatter.format(checkoutShipping)));
        orderDetails.setShippingMethod(shippingMethod.getName());
    
        orderDetails.setComments(checkout_comments.getText().toString().trim());
    
        if (couponsList.size() > 0) {
            orderDetails.setIsCouponApplied(1);
        } else {
            orderDetails.setIsCouponApplied(0);
        }
        orderDetails.setCouponAmount(Double.parseDouble(formatter.format(checkoutDiscount)));
        orderDetails.setCoupons(couponsList);
        
        Log.d("Nonce",paymentNonceToken);
        // Set PaymentNonceToken and PaymentMethod
        orderDetails.setNonce(paymentNonceToken);
        orderDetails.setPaymentMethod(selectedPaymentMethod);
    
        // Set Checkout Price and Products
        orderDetails.setProductsTotal(Double.parseDouble(formatter.format(checkoutSubtotal)));
        orderDetails.setTotalPrice(Double.parseDouble(formatter.format(checkoutTotal)));
        orderDetails.setProducts(orderProductList);
        
        
        PlaceOrderNow(orderDetails);
        
    }



    //*********** Request the Server to Generate BrainTreeToken ********//

    private void RequestPaymentMethods() {

        dialogLoader.showProgressDialog();

        Call<PaymentMethodsData> call = APIClient.getInstance()
                .getPaymentMethods
                        (
                                ConstantValues.LANGUAGE_ID
                        );


        call.enqueue(new Callback<PaymentMethodsData>() {
            @Override
            public void onResponse(Call<PaymentMethodsData> call, retrofit2.Response<PaymentMethodsData> response) {

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        for (int i=0;  i<response.body().getData().size();  i++) {

                            PaymentMethodsInfo paymentMethodsInfo = response.body().getData().get(i);
                            
                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("cod")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);
                            }
                           /* if (paymentMethodsInfo.getMethod().equalsIgnoreCase("hyperpay")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                
                                GenerateHyperPayTreeToken();
                                paymentMethodsList.add(paymentMethodsInfo);
                            }*/
                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("instamojo")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);
                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();
                                INSTAMOJO_PUBLISHABLE_KEY = paymentMethodsInfo.getPublicKey();
                            }
    
    
    
                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("paypal")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1"))
                            {
                                paymentMethodsList.add(paymentMethodsInfo);
                                
                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();
                                PAYPAL_PUBLISHABLE_KEY = paymentMethodsInfo.getPublicKey();
    
                                payPalConfiguration = new PayPalConfiguration()
                                    // sandbox (ENVIRONMENT_SANDBOX)
                                    // or live (ENVIRONMENT_PRODUCTION)
                                    .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                                    .clientId(PAYPAL_PUBLISHABLE_KEY);
    
                                Intent intent = new Intent(getContext(), PayPalService.class);
                                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
    
                                getContext().startService(intent);
                            }
                            

                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("stripe")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1"))
                            {
                                paymentMethodsList.add(paymentMethodsInfo);
                                STRIPE_PUBLISHABLE_KEY = paymentMethodsInfo.getPublicKey();
                            }


                            if ((paymentMethodsInfo.getMethod().equalsIgnoreCase("braintree_card")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1"))
                                    ||
                                    (paymentMethodsInfo.getMethod().equalsIgnoreCase("braintree_paypal")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")))
                            {
                                paymentMethodsList.add(paymentMethodsInfo);
                                
                                GenerateBrainTreeToken();

                            }
                            else {
                                dialogLoader.hideProgressDialog();
                            }

                        }

                    }
                    else {
                        // Unexpected Response from Server
                        dialogLoader.hideProgressDialog();
                        Snackbar.make(rootView, getString(R.string.cannot_get_payment_methods), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    dialogLoader.hideProgressDialog();
                    Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentMethodsData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    //*********** Request the Server to Generate HyperPayToken ********//
    
    private void GenerateHyperPayTreeToken() {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
    
        String email = userInfo.getCustomersEmailAddress();
        
        HashMap<String,String> transactionRequest = new HashMap<>();
        transactionRequest.put("amount",""+formatter.format(checkoutTotal));
        transactionRequest.put("email",email);
        
        Call<HyperPayToken> call = APIClient.getInstance()
                .getHyperPayToken(transactionRequest);
        
        
        call.enqueue(new Callback<HyperPayToken>() {
            @Override
            public void onResponse(Call<HyperPayToken> call, retrofit2.Response<HyperPayToken> response) {
                
                dialogLoader.hideProgressDialog();
                
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        hyperPayToken = response.body().getToken();
                      //  paymentNonceToken = hyperPayToken;
                        
                    }
                    else {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<HyperPayToken> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    //*********** Request the Server to Generate BrainTreeToken ********//

    private void GenerateBrainTreeToken() {

        Call<GetBrainTreeToken> call = APIClient.getInstance()
                .generateBraintreeToken();


        call.enqueue(new Callback<GetBrainTreeToken>() {
            @Override
            public void onResponse(Call<GetBrainTreeToken> call, retrofit2.Response<GetBrainTreeToken> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        braintreeToken = response.body().getToken();

                        // Initialize BraintreeFragment with BraintreeToken
                        try {
                            braintreeFragment = BraintreeFragment.newInstance(getActivity(), braintreeToken);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        Snackbar.make(rootView, getString(R.string.cannot_initialize_braintree), Snackbar.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.cannot_initialize_braintree), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBrainTreeToken> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    //*********** Request the Server to Generate BrainTreeToken ********//

    private void GetCouponInfo(String coupon_code) {

        Call<CouponsData> call = APIClient.getInstance()
                .getCouponInfo
                        (
                                coupon_code
                        );


        call.enqueue(new Callback<CouponsData>() {
            @Override
            public void onResponse(Call<CouponsData> call, retrofit2.Response<CouponsData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        final CouponsInfo couponsInfo = response.body().getData().get(0);
                        
                        if (couponsList.size() !=0 && couponsInfo.getIndividualUse().equalsIgnoreCase("1")) {
                            
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            
                            dialog.setTitle(getString(R.string.add_coupon));
                            dialog.setMessage(getString(R.string.coupon_removes_other_coupons));
                            
                            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
    
                                    if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent"))
                                    {
                                        if (validateCouponCart(couponsInfo))
                                                applyCoupon(couponsInfo);
                                        
                                    }
                                    else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product"))
                                    {
                                        if (validateCouponProduct(couponsInfo))
                                            applyCoupon(couponsInfo);
                                    }
                                }
                            });
                            
                            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            
                        }
                        else {
                            if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent"))
                            {
                                if (validateCouponCart(couponsInfo))
                                    applyCoupon(couponsInfo);
                                
                            }
                            else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product"))
                            {
                                if (validateCouponProduct(couponsInfo))
                                    applyCoupon(couponsInfo);
                            }
                        }

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        checkout_coupon_code.setError(response.body().getMessage());

                    } else {
                        // Unexpected Response from Server
                        Toast.makeText(getContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CouponsData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    //*********** Place the Order on the Server ********//
    
    public void LoadCheckoutPage(String url, final PostOrder postOrder) {
        
        progressDialog.show();
        Log.i("VC_Shop", "url= "+url);
        checkout_webView.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
       // main_checkOut.setVisibility(View.GONE);
       
      checkout_webView.setWebViewClient(new WebViewClient() {
           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url){
               // do your handling codes here, which url is the requested url
               // probably you need to open that url rather than redirect:
               view.loadUrl(url);
               return false; // then it is not handled by default action
           }
           
           @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i("order", "onPageStarted: url="+url);
             
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("VC_Shop", "onPageStarted: url= "+url);
                
                if (url.contains(SUCCESS)) {
                    view.stopLoading();
                    progressDialog.dismiss();
    
                    PlaceOrderNow(postOrder);
                   
                }
                
                else if(url.contains("error")){
                    Snackbar.make(rootView, "Error in payment method", Snackbar.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    checkout_webView.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialogLoader.hideProgressDialog();
                Log.i("VC_Shop", "onPageFinished: url= "+url);
            }
            
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                dialogLoader.hideProgressDialog();
                Log.i("VC_Shop", "onReceivedError: error= "+error);
            }
        });
    
        checkout_webView.setBackgroundColor(Color.TRANSPARENT);
        checkout_webView.getSettings().setJavaScriptEnabled(true);
        checkout_webView.getSettings().setLoadsImagesAutomatically(true);
        checkout_webView.getSettings().setDomStorageEnabled(true);
        checkout_webView.clearCache(true);
        checkout_webView.loadUrl(url);
    
    }
    
    //*********** Request the Server to Place User's Order ********//

    private void PlaceOrderNow(PostOrder postOrder) {
    
       
            String jsonObject = new Gson().toJson(postOrder);
        
    
        Call<OrderData> call = APIClient.getInstance()
                .addToOrder
                        (
                                postOrder
                        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {

                progressDialog.dismiss();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
    
                        Intent notificationIntent = new Intent(getContext(), MainActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        
                        // Order has been placed Successfully
                        NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), response.body().getMessage(), null);


                        // Clear User's Cart
                        My_Cart.ClearCart();

                        // Clear User's Shipping and Billing info from AppContext
                        ((App) getContext().getApplicationContext()).setShippingAddress(new AddressDetails());
                        ((App) getContext().getApplicationContext()).setBillingAddress(new AddressDetails());

                        
                        // Navigate to Thank_You Fragment
                        Fragment fragment = new Thank_You();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_fragment, fragment)
                                .commit();


                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
    
                    }
                    else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    //*********** Apply given Coupon to checkout ********//
    
    public void applyCoupon(CouponsInfo coupon) {
    
        double discount = 0.0;
    
        if (coupon.getDiscountType().equalsIgnoreCase("fixed_cart")) {
            discount = Double.parseDouble(coupon.getAmount());
            
        }
        else if (coupon.getDiscountType().equalsIgnoreCase("percent")) {
            discount = (checkoutSubtotal * Double.parseDouble(coupon.getAmount())) / 100;
            
        }
        else if (coupon.getDiscountType().equalsIgnoreCase("fixed_product")) {
            
            for (int i=0;  i<checkoutItemsList.size();  i++) {
                
                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());
                
    
                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")  ||  !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())  ||  coupon.getExcludedProductCategories().size() == 0 ) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())  ||  coupon.getExcludeProductIds().size() == 0 ) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())  ||  coupon.getProductCategories().size() == 0 ) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())  ||  coupon.getProductIds().size() == 0 ) {
                                    
                                    discount += (Double.parseDouble(coupon.getAmount()) * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }
    
                
            }
            
        }
        else if (coupon.getDiscountType().equalsIgnoreCase("percent_product")) {
            
            for (int i=0;  i<checkoutItemsList.size();  i++) {
        
                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                
                    String categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs();
                    if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")  ||  !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                        if (!isStringExistsInList(categoryID, coupon.getExcludedProductCategories())  ||  coupon.getExcludedProductCategories().size() == 0 ) {
                            if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())  ||  coupon.getExcludeProductIds().size() == 0 ) {
                                if (isStringExistsInList(categoryID, coupon.getProductCategories())  ||  coupon.getProductCategories().size() == 0 ) {
                                    if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())  ||  coupon.getProductIds().size() == 0 ) {
                        
                                        double discountOnPrice = (Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice()) * Double.parseDouble(coupon.getAmount())) / 100;
                                        discount += (discountOnPrice * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                    }
                                }
                            }
                        }
                }
                
                
        
            }
        }
        
        if ((checkoutDiscount+discount) >= getProductsSubTotal()) {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_be_applied));
        }
        else {
            if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
                couponsList.clear();
                checkoutDiscount = 0.0;
                checkoutShipping = checkoutShippingCost;
                disableOtherCoupons = true;
                setCheckoutTotal();
            }
    
            if (coupon.getFreeShipping().equalsIgnoreCase("1")) {
                checkoutShipping = 0.0;
            }
    
            
            checkoutDiscount += discount;
            coupon.setDiscount(String.valueOf(discount));
    
    
            couponsList.add(coupon);
            checkout_coupon_code.setText("");
            couponsAdapter.notifyDataSetChanged();
    
    
            setCheckoutTotal();
        }

    }
    
    
    //*********** Remove given Coupon from checkout ********//
    
    public void removeCoupon(CouponsInfo coupon) {
        
        if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
            disableOtherCoupons = false;
        }
    
    
        for (int i=0;  i<couponsList.size();  i++) {
            if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                couponsList.remove(i);
                couponsAdapter.notifyDataSetChanged();
            }
        }
    
        
        checkoutShipping = checkoutShippingCost;
    
        for (int i=0;  i<couponsList.size();  i++) {
            if (couponsList.get(i).getFreeShipping().equalsIgnoreCase("1")) {
                checkoutShipping = 0.0;
            }
        }
    
    
        double discount = Double.parseDouble(coupon.getDiscount());
        checkoutDiscount -= discount;
        
    
        setCheckoutTotal();

    }
    
    
    //*********** Validate Cart type Coupon ********//
    
    private boolean validateCouponCart(CouponsInfo coupon) {
    
        int user_used_this_coupon_counter = 0;
        
        boolean coupon_already_applied = false;
        
        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = true;
        
        boolean valid_items_in_cart = false;
        boolean valid_category_items_in_cart = false;
        
        boolean no_excluded_item_in_cart = true;
        boolean no_excluded_category_item_in_cart = true;
        
        
        if (couponsList.size() != 0) {
            for (int i=0;  i<couponsList.size();  i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }
        
        
        for (int i=0;  i<coupon.getUsedBy().size();  i++) {
            if (userInfo.getCustomersId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }
    
        
        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getCustomersEmailAddress(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        }
        else {
            valid_user_email_for_coupon = true;
        }
        
    
    
        for (int i=0;  i<checkoutItemsList.size();  i++) {
    
            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            String categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs();
            
    
            if (coupon.getExcludeSaleItems().equalsIgnoreCase("1") && checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = false;
            }
            
    
            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(categoryID, coupon.getExcludedProductCategories())) {
                    no_excluded_category_item_in_cart = false;
                }
            }
    
            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    no_excluded_item_in_cart = false;
                }
            }
    
            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(categoryID, coupon.getProductCategories())) {
                    valid_category_items_in_cart = true;
                }
            } else {
                valid_category_items_in_cart = true;
            }
    
            
            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    valid_items_in_cart = true;
                }
            } else {
                valid_items_in_cart = true;
            }
            
        }
        
        /////////////////////////////////////////////////////
        
        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                        if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                            if (valid_user_email_for_coupon) {
                                if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                    if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0  ||  checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                        if (valid_sale_items_in_for_coupon) {
                                            if (no_excluded_category_item_in_cart) {
                                                if (no_excluded_item_in_cart) {
                                                    if (valid_category_items_in_cart) {
                                                        if (valid_items_in_cart) {
                        
                                                            return true;
                        
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }

    }
    
    //*********** Validate Product type Coupon ********//
    
    private boolean validateCouponProduct(CouponsInfo coupon) {
        
        int user_used_this_coupon_counter = 0;
        
        boolean coupon_already_applied = false;
        
        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = false;
        
        boolean any_valid_item_in_cart = false;
        boolean any_valid_category_item_in_cart = false;
        
        boolean any_non_excluded_item_in_cart = false;
        boolean any_non_excluded_category_item_in_cart = false;
        
        
        if (couponsList.size() != 0) {
            for (int i=0;  i<couponsList.size();  i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }
        
        
        for (int i=0;  i<coupon.getUsedBy().size();  i++) {
            if (userInfo.getCustomersId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }
        
        
        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getCustomersEmailAddress(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        }
        else {
            valid_user_email_for_coupon = true;
        }
        
        for (int i=0;  i<checkoutItemsList.size();  i++) {
            
            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            String categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs();
            
            
            if (!coupon.getExcludeSaleItems().equalsIgnoreCase("1") || !checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = true;
            }
            
            
            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    any_non_excluded_category_item_in_cart = true;
                }
            } else {
                any_non_excluded_category_item_in_cart = true;
            }
            
            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    any_non_excluded_item_in_cart = true;
                }
            } else {
                any_non_excluded_item_in_cart = true;
            }
            
            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    any_valid_category_item_in_cart = true;
                }
            } else {
                any_valid_category_item_in_cart = true;
            }
            
            
            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    any_valid_item_in_cart = true;
                }
            } else {
                any_valid_item_in_cart = true;
            }
            
        }
        
        
        /////////////////////////////////////////////////////
        
        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                        if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                            if (valid_user_email_for_coupon) {
                                if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                    if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0  ||  checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                        if (valid_sale_items_in_for_coupon) {
                                            if (any_non_excluded_category_item_in_cart) {
                                                if (any_non_excluded_item_in_cart) {
                                                    if (any_valid_category_item_in_cart) {
                                                        if (any_valid_item_in_cart) {
                        
                                                            return true;
                        
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }
        
    }
    
    
    
    //*********** Show SnackBar with given Message  ********//
    
    private void showSnackBarForCoupon(String msg) {
        final Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
        snackbar.show();
    }
    
    
    
    //*********** Check if the given String exists in the given List ********//
    
    private boolean isStringExistsInList(String str, List<String> stringList) {
        boolean isExists = false;
    
        for (int i=0;  i<stringList.size();  i++) {
            if (stringList.get(i).equalsIgnoreCase(str)) {
                isExists = true;
            }
        }
        
        
        return isExists;
    }
    
    
    
    //*********** Setup Demo Coupons Dialog ********//
    
    private void setupDemoCoupons() {
    
        demo_coupons_text.setVisibility(View.VISIBLE);
        demo_coupons_text.setPaintFlags(demo_coupons_text.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        
        demo_coupons_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<CouponsInfo> couponsList = demoCouponsList();
                DemoCouponsListAdapter couponsListAdapter = new DemoCouponsListAdapter(getContext(), couponsList, Checkout.this);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                dialog_title.setText(getString(R.string.search) +" "+ getString(R.string.coupon));
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(couponsListAdapter);

                demoCouponsDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        demoCouponsDialog.dismiss();
                    }
                });

                demoCouponsDialog.show();
            }
        });
    }
    
    //*********** Sets selected Coupon code from the Dialog ********//
    
    public void setCouponCode(String code) {
        checkout_coupon_code.setText(code);
        demoCouponsDialog.dismiss();
    }
    
    
    
    //*********** Demo Coupons List ********//
    
    private List<CouponsInfo> demoCouponsList() {
        List<CouponsInfo> couponsList = new ArrayList<>();

        CouponsInfo coupon1 = new CouponsInfo();
        coupon1.setCode("PercentProduct_10");
        coupon1.setAmount("10");
        coupon1.setDiscountType("Percent Product");
        coupon1.setDescription("For All Products");

        CouponsInfo coupon2 = new CouponsInfo();
        coupon2.setCode("FixedProduct_10");
        coupon2.setAmount("10");
        coupon2.setDiscountType("Fixed Product");
        coupon2.setDescription("For All Products");

        CouponsInfo coupon3 = new CouponsInfo();
        coupon3.setCode("PercentCart_10");
        coupon3.setAmount("10");
        coupon3.setDiscountType("Percent Cart");
        coupon3.setDescription("For All Products");

        CouponsInfo coupon4 = new CouponsInfo();
        coupon4.setCode("FixedCart_10");
        coupon4.setAmount("10");
        coupon4.setDiscountType("Fixed Cart");
        coupon4.setDescription("For All Products");

        CouponsInfo coupon5 = new CouponsInfo();
        coupon5.setCode("SingleCoupon_50");
        coupon5.setAmount("50");
        coupon5.setDiscountType("Fixed Cart");
        coupon5.setDescription("Individual Use");

        CouponsInfo coupon6 = new CouponsInfo();
        coupon6.setCode("FreeShipping_20");
        coupon6.setAmount("20");
        coupon6.setDiscountType("Fixed Cart");
        coupon6.setDescription("Free Shipping");

        CouponsInfo coupon7 = new CouponsInfo();
        coupon7.setCode("ExcludeSale_15");
        coupon7.setAmount("15");
        coupon7.setDiscountType("Fixed Cart");
        coupon7.setDescription("Not for Sale Items");

        CouponsInfo coupon8 = new CouponsInfo();
        coupon8.setCode("Exclude_Shoes_25");
        coupon8.setAmount("25");
        coupon8.setDiscountType("Fixed Cart");
        coupon8.setDescription("Not For Men Shoes");

        CouponsInfo coupon9 = new CouponsInfo();
        coupon9.setCode("Polo_Shirts_10");
        coupon9.setAmount("10");
        coupon9.setDiscountType("Percent Product");
        coupon9.setDescription("For Men Polo Shirts");

        CouponsInfo coupon10 = new CouponsInfo();
        coupon10.setCode("Jeans_10");
        coupon10.setAmount("10");
        coupon10.setDiscountType("Percent Cart");
        coupon10.setDescription("For Men Jeans");


        couponsList.add(coupon1);
        couponsList.add(coupon2);
        couponsList.add(coupon3);
        couponsList.add(coupon4);
        couponsList.add(coupon5);
        couponsList.add(coupon6);
        couponsList.add(coupon7);
        couponsList.add(coupon8);
        couponsList.add(coupon9);
        couponsList.add(coupon10);

        return couponsList;
    }



    //*********** Validate Payment Card Inputs ********//

    private boolean validatePaymentCard() {
        if (!ValidateInputs.isValidNumber(checkout_card_number.getText().toString().trim())) {
            checkout_card_number.setError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidNumber(checkout_card_cvv.getText().toString().trim())) {
            checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (TextUtils.isEmpty(checkout_card_expiry.getText().toString().trim())) {
            checkout_card_expiry.setError(getString(R.string.select_card_expiry));
            return false;
        } else {
            return true;
        }
    }
    
    //*********** Validate Payment Info Inputs ********//
    
    private boolean validatePaymentInfo() {
        if (!ValidateInputs.isValidName(payment_name.getText().toString().trim())) {
            payment_name.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidEmail(payment_email.getText().toString().trim())) {
            payment_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPhoneNo(payment_phone.getText().toString().trim())) {
            payment_phone.setError(getString(R.string.invalid_contact));
            return false;
        } else {
            return true;
        }
    }

}

