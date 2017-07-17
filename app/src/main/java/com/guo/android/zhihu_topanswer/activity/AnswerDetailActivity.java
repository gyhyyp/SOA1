package com.guo.android.zhihu_topanswer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.guo.android.architecture.rxsupport.RxAppCompatActivity;
import com.guo.android.architecture.view.MultiStateView;
import com.guo.android.zhihu_topanswer.R;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;



public class AnswerDetailActivity extends RxAppCompatActivity {

    private static final String TAG = "AnswerDetailActivity";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DETAIL = "detail";
    private String mUrl;
    private WebView mWebView;
    private TextView mTitle;
    private ImageView mBack;
    private MultiStateView mStateView;
    private String title;
    private String bodyHtml;
    private TextView mDetail;
    private String detail;
    private View mPadding;
    private ImageView mWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_detail);
        mUrl = getIntent().getExtras().getString(URL);
        title = getIntent().getExtras().getString(TITLE);
        detail = getIntent().getExtras().getString(DETAIL);
        mTitle = (TextView) findViewById(R.id.answers_detail_title);
        mBack = (ImageView) findViewById(R.id.answers_detail_back);
        mWebView = (WebView) findViewById(R.id.activity_answers_detail_webview);
        mStateView = (MultiStateView) findViewById(R.id.activity_answers_detail_state);
        mDetail = (TextView) findViewById(R.id.activity_answers_detail_detail);
        mPadding = findViewById(R.id.activity_answers_detail_padding);
        mWeb = (ImageView) findViewById(R.id.answers_right);
        initView();
    }

    private void initView() {

        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mTitle.setText(title);
        if (TextUtils.isEmpty(detail)) {
            mPadding.setVisibility(View.GONE);
            mDetail.setVisibility(View.GONE);
        } else {
            mPadding.setVisibility(View.VISIBLE);
            mDetail.setVisibility(View.VISIBLE);
            mDetail.setText(Html.fromHtml(detail).toString());
        }
        mWeb.setClickable(true);
        mWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                startActivity(intent);
            }
        });
        mBack.setClickable(true);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerDetailActivity.this.finish();
            }
        });


        initData(true);
    }

    private void initData(final boolean needState) {

        if (needState) {
            mStateView.setViewState(MultiStateView.ViewState.LOADING);
        }
        final String url = mUrl;
        Observable.create(new Observable.OnSubscribe<Document>() {
            @Override
            public void call(Subscriber<? super Document> subscriber) {

                try {
                    subscriber.onNext(Jsoup.connect(url).timeout(5000).userAgent("Chrome/53.0.2785.143").get());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).map(new Func1<Document, String>() {
            @Override
            public String call(Document document) {


                Element bodyAnswer = document.getElementById("zh-question-answer-wrap");
                Elements bodys = bodyAnswer.select("div.zm-item-answer");
                Elements headElements = document.getElementsByTag("head");
                headElements.iterator().next();
                String head = headElements.iterator().next().outerHtml();

                String html = "";
                if (bodys.iterator().hasNext()) {
                    Iterator iterator = bodys.iterator();
                    if (iterator.hasNext()) {
                        Element element = (Element) iterator.next();
                        String body = "<body>" + element.select("div.zm-item-rich-text.expandable.js-collapse-body").iterator().next().outerHtml() + "</body>";
                        html = "<html lang=\"en\" xmlns:o=\"http://www.w3.org/1999/xhtml\">" + head + body + "</html>";

                        Document docu = Jsoup.parse(html);
                        Elements elements = docu.getElementsByTag("img");
                        Iterator iter = elements.iterator();
                        while (iter.hasNext()) {
                            Element imgElement = (Element) iter.next();
                            String result = imgElement.attr("data-actualsrc");
                            if (TextUtils.isEmpty(result)) {
                                result = imgElement.attr("data-original");
                            }
                            imgElement.attr("src", result);
                        }
                        html = docu.outerHtml();
                        return html;

                    }
                }
                return "";

            }
        }).compose(this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {


                    @Override
                    public void onCompleted() {

                        if (mStateView.getViewState() != MultiStateView.ViewState.CONTENT) {
                            mStateView.setViewState(MultiStateView.ViewState.CONTENT);
                        }
                        mWebView.loadDataWithBaseURL("http://www.zhihu.com", bodyHtml, "text/html", "utf-8", null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (needState) {
                            mStateView.setViewState(MultiStateView.ViewState.ERROR);
                        }


                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(String s) {

                        bodyHtml = s;

                    }
                });
    }
}
