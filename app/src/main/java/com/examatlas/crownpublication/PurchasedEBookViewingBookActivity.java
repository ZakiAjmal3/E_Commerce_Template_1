package com.examatlas.crownpublication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

public class PurchasedEBookViewingBookActivity extends AppCompatActivity {

    String bookIdByIntent, bookTitleStr;
    String ebookURL;
    Dialog progressDialog;
    TextView bookTitleTxt;
    ImageView backBtn;
    WebView eBookWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_ebook_viewing);

        progressDialog = new Dialog(PurchasedEBookViewingBookActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();

        backBtn = findViewById(R.id.backBtn);
        bookTitleTxt = findViewById(R.id.bookTitleTxt);

        eBookWebView = findViewById(R.id.webView);
        // Enable JavaScript (optional depending on your content)
        eBookWebView.getSettings().setJavaScriptEnabled(true);
        // Disable text selection and copying via JavaScript injection
        String disableCopyJs = "document.body.style.userSelect = 'none';" +
                "document.body.style.webkitUserSelect = 'none';" +
                "document.body.style.MozUserSelect = 'none';" +
                "document.body.style.msUserSelect = 'none';" +
                "document.body.addEventListener('copy', function(e) { e.preventDefault(); });";
        // Load content into WebView
        eBookWebView.loadUrl("file:///android_asset/sample.html"); // Load your HTML content
        eBookWebView.evaluateJavascript(disableCopyJs, null);
        // Disable long click (context menu)
        eBookWebView.setOnLongClickListener(v -> true);
        // Allow scrolling by NOT disabling touch events or scrolling
        eBookWebView.setFocusable(true);
        eBookWebView.setClickable(true);
        eBookWebView.setLongClickable(true);
        // Set up WebView Client and Chrome Client for better behavior (Optional)
        eBookWebView.setWebViewClient(new WebViewClient());
        eBookWebView.setWebChromeClient(new WebChromeClient());
        eBookWebView.getSettings().setDomStorageEnabled(true);
        eBookWebView.getSettings().setAllowFileAccess(true);
        eBookWebView.getSettings().setLoadWithOverviewMode(true);
        eBookWebView.getSettings().setUseWideViewPort(true);
        eBookWebView.getSettings().setSupportZoom(true);
        eBookWebView.getSettings().setBuiltInZoomControls(true);

        bookIdByIntent = getIntent().getStringExtra("bookId");
        bookTitleStr = getIntent().getStringExtra("title");
        bookTitleTxt.setText(bookTitleStr);
        bookTitleTxt.setVisibility(View.VISIBLE);

        // Set the flag to prevent screenshots or screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        backBtn.setOnClickListener(view -> onBackPressed());

        // Fetch the ebook details and download URL
        getEBookById();

    }

    private void getEBookById() {
        String singleBookURL = Constant.BASE_URL + "booksByID?id=" + bookIdByIntent;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, singleBookURL, null,
                response -> {
                    try {
                        boolean status = response.getBoolean("success");
                        if (status) {
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("ebookFiles");
                            ebookURL = jsonArray.getJSONObject(0).getString("url");
                            Log.d("ebookURL", ebookURL);
                            downloadEpubFile(ebookURL);
//                            downloadEpubFile("https://storage.googleapis.com/crown_bucket/1739253656268_0tm7g-1mbj3.epub");
                        }
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Error: " + error.toString();
                    if (error.networkResponse != null) {
                        try {
                            String jsonError = new String(error.networkResponse.data);
                            JSONObject jsonObject = new JSONObject(jsonError);
                            String message = jsonObject.optString("message", "Unknown error");
                            Toast.makeText(PurchasedEBookViewingBookActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("book error", errorMessage);
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void downloadEpubFile(String url) {
        // Use HttpURLConnection to download the EPUB file as InputStream
        new Thread(() -> {
            try {
                // Create a URL object from the ebook URL
                URL epubUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) epubUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // Timeout for connection
                connection.setReadTimeout(5000); // Timeout for data download
                Log.d("EBookDownload", "Starting download...");
                // Open InputStream
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                // Save the file to local storage
                File epubFile = new File(getFilesDir(), "downloaded.epub");
                FileOutputStream fileOutputStream = new FileOutputStream(epubFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                }

                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();

                Log.d("EBookDownload", "EPUB downloaded successfully!");

                // Now, you can process the EPUB file to open it in WebView (Next steps).
                readEpubFile(epubFile);

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(PurchasedEBookViewingBookActivity.this, "Error downloading EPUB file.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    private void readEpubFile(File file) {
        try {
            // Use EPUBlib to read the downloaded EPUB file
            Book book = (new EpubReader()).readEpub(new FileInputStream(file));

            // Extract content from the EPUB file
            StringBuilder content = new StringBuilder();
            for (Resource resource : book.getContents()) {
                // Check for XHTML resources and convert them to string
                if (resource.getMediaType().toString().equals("application/xhtml+xml")) {
                    byte[] resourceData = resource.getData();  // Get resource data as byte[]
                    String bodyContent = new String(resourceData, StandardCharsets.UTF_8);  // Convert to String
                    content.append(bodyContent);  // Append the body content
                }
            }
            Log.d("EPUB_CONTENT", "Extracted content: " + content.toString());
            // Display the extracted content in the WebView
            displayContent(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read EPUB file", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display EPUB content in a WebView
    private void displayContent(String content) {
        Log.d("EPUB_CONTENT", "Content to display in WebView: " + content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                eBookWebView.setVisibility(View.VISIBLE);
                eBookWebView.loadData(content, "text/html", "UTF-8");
                progressDialog.dismiss();
            }
        });

    }

//    private void openEpubInWebView(File epubFile) {
//        try {
//            // Extract the EPUB content
//            File extractedDir = new File(getFilesDir(), "extracted_epub");
//            if (!extractedDir.exists()) {
//                extractedDir.mkdirs();
//            }
//
//            unzipEpub(epubFile, extractedDir);
//
//            // List all the files in the extracted directory for debugging
//            String[] files = extractedDir.list();
//            if (files != null) {
//                for (String file : files) {
//                    Log.d("EPUB_EXTRACT", "Extracted file: " + file);
//                }
//            }
//
//            // Look for the first HTML or XHTML file in the extracted folder
//            File htmlFile = findHtmlFile(extractedDir);
//            if (htmlFile != null) {
//                String content = readFile(htmlFile);
//                // Log the HTML content to ensure it's not empty
//                Log.d("EPUB_EXTRACT", "HTML content: " + content);
//
//                // Load the HTML content into WebView
//                runOnUiThread(() -> {
//                    eBookWebView.setVisibility(View.VISIBLE);
//                    eBookWebView.getSettings().setJavaScriptEnabled(true);
//                    eBookWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//                    eBookWebView.getSettings().setAllowFileAccessFromFileURLs(true);
//                    eBookWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//                    eBookWebView.setLongClickable(true);
//                    eBookWebView.setHapticFeedbackEnabled(true);
//
//                    // Set the base URL to the extracted folder
//                    eBookWebView.loadDataWithBaseURL("file:///" + extractedDir.getAbsolutePath() + "/", content, "text/html", "UTF-8", null);
//
//                    // Enable debugging to check for issues in the WebView
//                    WebView.setWebContentsDebuggingEnabled(true);
//                    progressDialog.dismiss();
//                    bookTitleTxt.setVisibility(View.VISIBLE);
//                });
//            } else {
//                progressDialog.dismiss();
//                Log.e("EPUB_EXTRACT", "HTML file not found!");
//                runOnUiThread(() -> {
//                    Toast.makeText(PurchasedEBookViewingBookActivity.this, "EPUB format error: HTML file not found.", Toast.LENGTH_LONG).show();
//                });
//            }
//
//        } catch (Exception e) {
//            progressDialog.dismiss();
//            e.printStackTrace();
//            Log.e("EPUB_EXTRACT", "Error loading EPUB content", e);
//        }
//    }
//
//
//
//    private File findHtmlFile(File extractedDir) {
//        // Look for the first HTML or XHTML file in the extracted EPUB
//        File[] files = extractedDir.listFiles();
//        for (File file : files) {
//            if (file.isDirectory()) {
//                File foundHtml = findHtmlFile(file);
//                if (foundHtml != null) {
//                    return foundHtml;
//                }
//            } else if (file.getName().endsWith(".html") || file.getName().endsWith(".xhtml")) {
//                return file;
//            }
//        }
//        return null; // No HTML file found
//    }
//
//    private String readFile(File file) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        reader.close();
//        return stringBuilder.toString();
//    }
//
//    private void unzipEpub(File epubFile, File destinationDir) throws IOException {
//        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(epubFile));
//        ZipEntry entry;
//
//        while ((entry = zipInputStream.getNextEntry()) != null) {
//            File entryFile = new File(destinationDir, entry.getName());
//
//            // Log entry information to check the extraction
//            Log.d("EPUB_EXTRACT", "Extracting: " + entry.getName());
//
//            if (entry.isDirectory()) {
//                if (!entryFile.exists()) {
//                    entryFile.mkdirs();
//                }
//            } else {
//                File parentDir = entryFile.getParentFile();
//                if (parentDir != null && !parentDir.exists()) {
//                    parentDir.mkdirs();
//                }
//
//                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(entryFile));
//                byte[] buffer = new byte[1024];
//                int len;
//                while ((len = zipInputStream.read(buffer)) != -1) {
//                    out.write(buffer, 0, len);
//                }
//                out.close();
//            }
//
//            zipInputStream.closeEntry();
//        }
//        zipInputStream.close();
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove FLAG_SECURE when the activity is destroyed
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}
