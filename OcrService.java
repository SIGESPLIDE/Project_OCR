import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OcrService {

    /**
     * 指定された画像ファイルから文字を検出する
     * @param imagePath 画像ファイルのパス
     * @return 検出されたテキスト全体
     * @throws IOException
     */
    public String detectText(String imagePath) throws IOException {
        // ImageAnnotatorClientのインスタンスを作成
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            // 画像ファイルを読み込む
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(imagePath));
            Image img = Image.newBuilder().setContent(imgBytes).build();

            // 検出したい機能のタイプ（今回はテキスト検出）を指定
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            // リクエストをリストにまとめる（複数の画像も一度に処理可能）
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            // APIにリクエストを送信
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            // レスポンスを処理
            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.err.println("Error: " + res.getError().getMessage());
                    return "エラーが発生しました: " + res.getError().getMessage();
                }

                // 検出されたテキスト全体を返す
                return res.getFullTextAnnotation().getText();
            }
        }
        return "テキストが検出されませんでした。";
    }
}