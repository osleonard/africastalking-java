package com.africastalking;


import com.africastalking.sms.FetchMessageResponse;
import com.africastalking.sms.FetchSubscriptionResponse;
import com.africastalking.sms.Message;
import com.africastalking.sms.Recipient;
import com.africastalking.sms.SendMessageResponse;
import com.africastalking.sms.Subscription;
import com.africastalking.sms.SubscriptionResponse;

import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;


/**
 * SMS Service; Send and fetch SMSs
 */
public final class SmsService extends Service {


    private static SmsService sInstance;
    private ISMS sms;

    private SmsService(String username, String apiKey) {
        super(username, apiKey);
    }

    SmsService() {
        super();
    }

    @Override
    protected SmsService getInstance(String username, String apiKey) {

        if (sInstance == null) {
            sInstance = new SmsService(username, apiKey);
        }

        return sInstance;
    }

    @Override
    protected void initService() {
        String baseUrl = "https://api."+ (isSandbox ? Const.SANDBOX_DOMAIN : Const.PRODUCTION_DOMAIN) + "/version1/";
        sms = mRetrofitBuilder.baseUrl(baseUrl).build().create(ISMS.class);
    }

    @Override
    protected boolean isInitialized() {
        return sInstance != null;
    }

    @Override
    protected void destroyService() {
        if (sInstance != null) {
            sInstance = null;
        }
    }

    private String formatRecipients(String[] recipients) {

        if (recipients == null){
            return null;
        }

        if (recipients.length == 1) {
            return recipients[0];
        }

        StringJoiner joiner = new StringJoiner(",");
        for (CharSequence cs: recipients) {
            joiner.add(cs);
        }
        return joiner.toString();
    }

    // -> Normal

    /**
     * Send a message
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param from
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> send(String message, String from, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.send(mUsername, formatRecipients(recipients), from, message).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body().data.recipients;
    }

    /**
     * Send a message
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param from
     * @param recipients
     * @param callback
     */
    public void send(String message, String from, String[] recipients, final Callback<List<Recipient>> callback) {
        sms.send(mUsername, formatRecipients(recipients), from, message).enqueue(makeCallback(new Callback<SendMessageResponse>() {
            @Override
            public void onSuccess(SendMessageResponse data) {
                callback.onSuccess(data.data.recipients);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));
    }

    /**
     * Send a message
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> send(String message, String[] recipients) throws IOException {
        return send(message, null, recipients);
    }


    /**
     * Send a message
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param recipients
     * @param callback
     */
    public void send(String message, String[] recipients, Callback<List<Recipient>> callback) {
        send(message, null, recipients, callback);
    }


    // -> Bulk

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param from
     * @param enqueue
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendBulk(String message, String from, boolean enqueue, String[] recipients) throws IOException {
        Response<SendMessageResponse> resp = sms.sendBulk(mUsername,
                formatRecipients(recipients), from, message,
                1, enqueue ? "1":null).execute();

        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body().data.recipients;
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param from
     * @param enqueue
     * @param recipients
     * @param callback
     */
    public void sendBulk(String message, String from, boolean enqueue, String[] recipients, final Callback<List<Recipient>> callback) {
        sms.sendBulk(mUsername,
                formatRecipients(recipients), from, message,
                1, enqueue ? "1":null).enqueue(makeCallback(new Callback<SendMessageResponse>() {
            @Override
            public void onSuccess(SendMessageResponse data) {
                callback.onSuccess(data.data.recipients);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));
    }

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param from
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendBulk(String message, String from, String[] recipients) throws IOException {
        return sendBulk(message, from, false, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param from
     * @param recipients
     * @param callback
     */
    public void sendBulk(String message, String from, String[] recipients, Callback<List<Recipient>> callback) {
        sendBulk(message, from, false, recipients, callback);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param enqueue
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendBulk(String message, boolean enqueue, String[] recipients) throws IOException {
        return sendBulk(message, null, enqueue, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param enqueue
     * @param recipients
     * @param callback
     */
    public void sendBulk(String message, boolean enqueue, String[] recipients, Callback<List<Recipient>> callback) {
        sendBulk(message, null, enqueue, recipients, callback);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendBulk(String message, String[] recipients) throws IOException {
        return sendBulk(message, null, false, recipients);
    }

    /**
     * Send a message in bulk
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param recipients
     * @param callback
     */
    public void sendBulk(String message, String[] recipients, Callback<List<Recipient>> callback) {
        sendBulk(message, null, false, recipients, callback);
    }


    // -> Premium

    /**
     * Send premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param from
     * @param keyword
     * @param linkId
     * @param retryDurationInHours
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients) throws IOException {
        String retryDuration = retryDurationInHours <= 0 ? null : String.valueOf(retryDurationInHours);
        Response<SendMessageResponse> resp = sms.sendPremium(mUsername, formatRecipients(recipients), from, message, keyword, linkId, retryDuration, 0).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body().data.recipients;
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param from
     * @param keyword
     * @param linkId
     * @param retryDurationInHours
     * @param recipients
     * @param callback
     */
    public void sendPremium(String message, String from, String keyword, String linkId, long retryDurationInHours, String[] recipients, final Callback<List<Recipient>> callback) {
        String retryDuration = retryDurationInHours <= 0 ? null : String.valueOf(retryDurationInHours);
        sms.sendPremium(mUsername, formatRecipients(recipients),
                from, message, keyword, linkId, retryDuration, 0)
                .enqueue(makeCallback(new Callback<SendMessageResponse>() {
                    @Override
                    public void onSuccess(SendMessageResponse data) {
                        callback.onSuccess(data.data.recipients);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailure(throwable);
                    }
                }));
    }

    /**
     * Send premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param keyword
     * @param linkId
     * @param retryDurationInHours
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients) throws IOException {
        return sendPremium(message, null, keyword, linkId, retryDurationInHours, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param keyword
     * @param linkId
     * @param retryDurationInHours
     * @param recipients
     * @param callback
     */
    public void sendPremium(String message, String keyword, String linkId, long retryDurationInHours, String[] recipients, Callback<List<Recipient>> callback){
        sendPremium(message, null, keyword, linkId, retryDurationInHours, recipients, callback);
    }

    /**
     * Send Premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param from
     * @param keyword
     * @param linkId
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendPremium(String message, String from, String keyword, String linkId, String[] recipients) throws IOException {
        return sendPremium(message, from, keyword, linkId, -1, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param from
     * @param keyword
     * @param linkId
     * @param recipients
     * @param callback
     */
    public void sendPremium(String message, String from, String keyword, String linkId, String[] recipients, Callback<List<Recipient>> callback){
        sendPremium(message, from, keyword, linkId, -1, recipients, callback);
    }

    /**
     * Send premium SMS
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param message
     * @param keyword
     * @param linkId
     * @param recipients
     * @return
     * @throws IOException
     */
    public List<Recipient> sendPremium(String message, String keyword, String linkId, String[] recipients) throws IOException {
        return sendPremium(message, null, keyword, linkId, -1, recipients);
    }

    /**
     * Send premium SMS
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param message
     * @param keyword
     * @param linkId
     * @param recipients
     * @param callback
     */
    public void sendPremium(String message, String keyword, String linkId, String[] recipients, Callback<List<Recipient>> callback){
        sendPremium(message, null, keyword, linkId, -1, recipients, callback);
    }

    // -> Fetch Message

    /**
     * Fetch messages
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param lastReceivedId
     * @return
     * @throws IOException
     */
    public List<Message> fetchMessage(String lastReceivedId) throws IOException {
        Response<FetchMessageResponse> resp = sms.fetchMessage(mUsername, lastReceivedId).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body().data.messages;
    }

    /**
     * Fetch messages
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @return
     * @throws IOException
     */
    public List<Message> fetchMessage() throws IOException {
        return fetchMessage("0");
    }

    /**
     * Fetch messages
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param lastReceivedId
     * @param callback
     */
    public void fetchMessage(String lastReceivedId, final Callback<List<Message>> callback) {
        sms.fetchMessage(mUsername, lastReceivedId).enqueue(makeCallback(new Callback<FetchMessageResponse>() {
            @Override
            public void onSuccess(FetchMessageResponse data) {
                callback.onSuccess(data.data.messages);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));
    }

    /**
     * Fetch messages
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param callback
     */
    public void fetchMessage(Callback<List<Message>> callback) {
        fetchMessage("0", callback);
    }

    // -> Fetch Subscription

    /**
     * Fetch subscriptions
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param shortCode
     * @param keyword
     * @param lastReceivedId
     * @return
     * @throws IOException
     */
    public List<Subscription> fetchSubscription(String shortCode, String keyword, String lastReceivedId) throws IOException {
        Response<FetchSubscriptionResponse> resp = sms.fetchSubscription(mUsername, shortCode, keyword, lastReceivedId).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body().subscriptions;
    }

    /**
     * Fetch subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param shortCode
     * @param keyword
     * @param lastReceivedId
     * @param callback
     */
    public void fetchSubscription(String shortCode, String keyword, String lastReceivedId, final Callback<List<Subscription>> callback) {
        sms.fetchSubscription(mUsername, shortCode, keyword, lastReceivedId).enqueue(makeCallback(new Callback<FetchSubscriptionResponse>() {
            @Override
            public void onSuccess(FetchSubscriptionResponse data) {
                callback.onSuccess(data.subscriptions);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        }));

    }

    /**
     * Fetch subscriptions
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param shortCode
     * @param keyword
     * @return
     * @throws IOException
     */
    public List<Subscription> fetchSubscription(String shortCode, String keyword) throws IOException {
        return fetchSubscription(shortCode, keyword, "0");
    }


    /**
     * Create subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param shortCode
     * @param keyword
     * @param callback
     */
    public void fetchSubscription(String shortCode, String keyword, Callback<List<Subscription>> callback) {
        fetchSubscription(shortCode, keyword, "0", callback);
    }

    // -> Create subscription

    /**
     * Create subscription
     * <p>
     *     Synchronously send the request and return its response.
     * </p>
     * @param shortCode
     * @param keyword
     * @param phoneNumber
     * @param checkoutToken
     * @return
     * @throws IOException
     */
    public SubscriptionResponse createSubscription(String shortCode, String keyword, String phoneNumber, String checkoutToken) throws IOException {
        Response<SubscriptionResponse> resp = sms.createSubscription(mUsername, shortCode, keyword, phoneNumber, checkoutToken).execute();
        if (!resp.isSuccessful()) {
            throw new IOException(resp.errorBody().string());
        }
        return resp.body();
    }

    /**
     * Create subscription
     * <p>
     *     Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred
     * </p>
     * @param shortCode
     * @param keyword
     * @param phoneNumber
     * @param checkoutToken
     * @param callback
     */
    public void createSubscription(String shortCode, String keyword, String phoneNumber, String checkoutToken, Callback<SubscriptionResponse> callback) {
        sms.createSubscription(mUsername, shortCode, keyword, phoneNumber, checkoutToken).enqueue(makeCallback(callback));
    }


}
