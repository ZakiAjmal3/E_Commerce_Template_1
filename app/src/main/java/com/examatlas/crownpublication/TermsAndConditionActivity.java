package com.examatlas.crownpublication;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TermsAndConditionActivity extends AppCompatActivity {
    WebView webView;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webView = findViewById(R.id.webView);

//         Enable JavaScript (optional, depending on your content)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Disable scrolling and over-scrolling
        webView.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

        String contentStr = " <div className=\"p-6 md:p-10 max-w-4xl mx-auto text-gray-700 mt-20 mb-20\">\n" +
                "      <div className=\"space-y-6\">\n" +
                "        <section>\n" +
                "          <h2 className=\"text-2xl font-semibold mb-2\">Welcome to Crown Publications</h2>\n" +
                "          <p>\n" +
                "            You are welcome to <strong>crownpublications.in</strong>. You can avail the\n" +
                "            services offered here or through its affiliates, but prior to that, you\n" +
                "            need to agree to the terms and conditions. If you browse our Online\n" +
                "            Bookstore, you have to accept these conditions.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section>\n" +
                "          <h2 className=\"text-2xl font-semibold mb-2\">Privacy Policy</h2>\n" +
                "          <p>\n" +
                "            Make sure that you thoroughly review the privacy policy that governs\n" +
                "            the visit to our site.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section>\n" +
                "          <h2 className=\"text-2xl font-semibold mb-2\">Modification of Orders</h2>\n" +
                "          <p>\n" +
                "            If there is a change in quantity or addition of items or if specific\n" +
                "            changes have been accepted, we can modify the details of the orders.\n" +
                "            All the sales are final, and cancellations of the items can be made\n" +
                "            before the order is finally shipped. Without any liability, we might\n" +
                "            cancel the accepted order before the shipment if the credit department\n" +
                "            does not give the approval of your credit. We can do the same if there\n" +
                "            is a problem with the mode of payment that you have selected.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section>\n" +
                "          <h2 className=\"text-2xl font-semibold mb-2\">Risk of Loss</h2>\n" +
                "          <p>\n" +
                "            All items purchased from Crown Publications are made pursuant to a\n" +
                "            shipment contract. This means that the risk of loss and title for such\n" +
                "            items passes to you upon our delivery to the carrier.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "      </div>\n" +
                "    </div>";

        // Load the modified HTML content
        webView.loadData(contentStr, "text/html", "UTF-8");

    }
}