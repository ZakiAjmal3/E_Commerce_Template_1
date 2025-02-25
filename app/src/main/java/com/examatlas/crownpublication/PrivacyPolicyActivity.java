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

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView webView;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

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

        String contentStr = " <div className=\"min-h-screen  py-8 px-4 sm:px-6 lg:px-8 mt-20 mb-20\">\n" +
                "        </h1>\n" +
                "        <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "          <strong>Effective Date:</strong> 24-02-2025\n" +
                "        </p>\n" +
                "        <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "          <strong>Welcome to Crown Publications (“Company,” “we,” “our,” “us”)</strong>. Your privacy is important to us. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you visit our website [crownpublications.in](http://crownpublications.in), use our services, or engage with us in other ways. Please read this policy carefully. By using our website and services, you agree to the collection and use of information in accordance with this policy.\n" +
                "        </p>\n" +
                "        \n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">1. Information We Collect</h2>\n" +
                "          <ul className=\"list-disc pl-6 text-gray-700\">\n" +
                "            <li>Personal Data: When you purchase a product, subscribe to a newsletter, or engage with our services, we may collect personal details such as your name, email address, phone number, billing address, and payment information (credit card or debit card details).</li>\n" +
                "            <li>Usage Data: We collect data about how you interact with our website, such as your IP address, browser type, pages viewed, and the time spent on our site.</li>\n" +
                "            <li>Cookies and Tracking Technologies: We use cookies to enhance your experience, analyze trends, and gather information about site traffic. You can control cookies through your browser settings.</li>\n" +
                "          </ul>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">2. How We Use Your Information</h2>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            We use the information we collect for various purposes, including:\n" +
                "          </p>\n" +
                "          <ul className=\"list-disc pl-6 text-gray-700\">\n" +
                "            <li>To provide, operate, and maintain our website and services.</li>\n" +
                "            <li>To process your purchases, orders, and payments.</li>\n" +
                "            <li>To send marketing and promotional materials, newsletters, and other communications that you may opt out of at any time.</li>\n" +
                "            <li>To improve, personalize, and optimize our services and website based on user behavior and feedback.</li>\n" +
                "            <li>To detect, prevent, and address technical issues and security breaches.</li>\n" +
                "          </ul>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">3. Sharing Your Information</h2>\n" +
                "          <ul className=\"list-disc pl-6 text-gray-700\">\n" +
                "            <li>With Service Providers: We may share your information with third-party vendors who assist us in operating our business and providing services to you, such as payment processors and email service providers.</li>\n" +
                "            <li>For Legal Compliance: We may disclose your information if required by law or in response to valid legal requests from government authorities or law enforcement agencies.</li>\n" +
                "            <li>In Business Transfers: If we are involved in a merger, acquisition, or asset sale, your information may be transferred as part of that transaction. We will notify you before your personal information becomes subject to a different privacy policy.</li>\n" +
                "          </ul>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">4. Your Rights and Choices</h2>\n" +
                "          <ul className=\"list-disc pl-6 text-gray-700\">\n" +
                "            <li>Access: You may request access to the personal information we hold about you.</li>\n" +
                "            <li>Correction: You may request to update or correct any inaccurate or incomplete information.</li>\n" +
                "            <li>Deletion: You may request the deletion of your personal information, subject to legal obligations.</li>\n" +
                "            <li>Opt-out of Marketing: You can opt out of receiving promotional emails by following the unsubscribe link in our communications or by contacting us directly at info@crownpublications.in.</li>\n" +
                "          </ul>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">5. Data Security</h2>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            We implement appropriate technical and organizational measures to protect your personal information from unauthorized access, alteration, or destruction. However, please note that no method of transmission over the Internet or electronic storage is completely secure, and we cannot guarantee absolute security.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">6. Children’s Privacy</h2>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            Our services are intended for individuals who are at least 18 years old. We do not knowingly collect or solicit personal information from anyone under the age of 18. If we learn that we have inadvertently collected information from a child under 18, we will take steps to delete such information from our records.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">7. Third-Party Links</h2>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            Our website may contain links to third-party websites or services that are not owned or controlled by Crown Publications. We are not responsible for the content, privacy policies, or practices of these third parties. We encourage you to review the privacy policies of any third-party websites you visit.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section className=\"mb-8\">\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">8. Changes to This Privacy Policy</h2>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the \"Effective Date\" at the top of this policy. We encourage you to review this Privacy Policy periodically for any updates.\n" +
                "          </p>\n" +
                "        </section>\n" +
                "\n" +
                "        <section>\n" +
                "          <h2 className=\"text-xl font-semibold text-gray-900 mb-4\">9. Contact Us</h2>\n" +
                "          <p className=\"text-lg text-gray-700\">\n" +
                "            If you have any questions about this Privacy Policy or how we handle your personal information, please contact us at:\n" +
                "          </p>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            <strong>Email:</strong> support@crownpublications.in\n" +
                "          </p>\n" +
                "          <p className=\"text-lg text-gray-700 mb-4\">\n" +
                "            <strong>Phone:</strong> [9153434753]\n" +
                "          </p>\n" +
                "          <p className=\"text-lg text-gray-700\">\n" +
                "            <strong>Website:</strong> [crownpublications.in](http://crownpublications.in)\n" +
                "          </p>\n" +
                "        </section>\n" +
                "      </div>\n" +
                "    </div>";

        // Load the modified HTML content
        webView.loadData(contentStr, "text/html", "UTF-8");

    }
}