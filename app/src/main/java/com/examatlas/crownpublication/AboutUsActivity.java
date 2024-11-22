package com.examatlas.crownpublication;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUsActivity extends AppCompatActivity {
    WebView webView;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

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

        String contentStr = "    <div className=\"p-6 md:p-10 mt-20 mb-20\">\n" +
                "  <div className=\"space-y-6 text-gray-700\">\n" +
                "    <section>\n" +
                "      <h2 className=\"text-2xl font-semibold\">Crown Publications</h2>\n" +
                "      <p>\n" +
                "        Crown Publications is widely recognized as one of the leading competitive book publishers in India. With years of dedication and expertise, we specialize in creating high-quality resources for aspirants of various examinations, including UPSC, State Public Service Commissions, SSC, Banking, Law, Departmental, and other competitive exams.\n" +
                "      </p>\n" +
                "    </section>\n" +
                "    <section>\n" +
                "      <h2 className=\"text-2xl font-semibold\">Our Commitment to Quality</h2>\n" +
                "      <p>\n" +
                "        At Crown Publications, we have always prioritized maintaining the highest standards in content quality and production. Our books are meticulously designed to meet the dynamic needs of competitive exam aspirants and are available in both English and Hindi mediums.\n" +
                "      </p>\n" +
                "    </section>\n" +
                "    <section>\n" +
                "      <h2 className=\"text-2xl font-semibold\">Why Choose Crown Publications?</h2>\n" +
                "      <ul className=\"list-disc ml-6 space-y-2\">\n" +
                "        <li><strong>Rich in Content:</strong> Comprehensive and detailed material to enhance learning.</li>\n" +
                "        <li><strong>Contemporary in Orientation:</strong> Updated with the latest syllabus and trends.</li>\n" +
                "        <li><strong>Student-Friendly:</strong> Designed to be easily understandable and effective for students.</li>\n" +
                "      </ul>\n" +
                "      <p>\n" +
                "        This unique combination of quality and relevance has made Crown Publications a trusted name among teachers and students alike. Our books are widely recommended by educators and highly preferred by students preparing for various competitive examinations.\n" +
                "      </p>\n" +
                "    </section>\n" +
                "    <section>\n" +
                "      <h2 className=\"text-2xl font-semibold\">Our Vision</h2>\n" +
                "      <p>\n" +
                "        To empower aspirants with resources that make achieving their career goals possible. We believe in delivering excellence and ensuring that every student has access to high-quality materials.\n" +
                "      </p>\n" +
                "    </section>\n" +
                "  </div>\n" +
                "</div>";

        // Load the modified HTML content
        webView.loadData(contentStr, "text/html", "UTF-8");

    }
}