package actions.views;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * いいね情報について画面の入力値・出力値を扱うViewモデル
 *
 */

@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor  //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)

public class GoodView {

//    public GoodView(Object object, EmployeeView ev, ReportView rv, LocalDateTime day) {
//    }

    /**
     * id
     */
    private Integer id;

    /**
     * いいねした従業員のid
     */
    private EmployeeView employee;

    /**
     * いいねする日報のid
     */
    private ReportView report;

    /**
     * 登録日時
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    private LocalDateTime updatedAt;
}
