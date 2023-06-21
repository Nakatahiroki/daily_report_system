package models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * いいねデータのDTOモデル
 *
 */
@Table(name = "goods")
@NamedQueries({
    //全てのいいねをidの降順に取得する
    @NamedQuery(
            name = "good.getAll",
            query = "SELECT g FROM Good AS g ORDER BY g.id DESC"
            ),
    //全てのいいねの件数を取得する
    @NamedQuery(
            name = "good.Count",
            query = "SELECT  COUNT(g) FROM Good AS g"
            ),
    //指定した日報にいいねした人のデータを取得
    @NamedQuery(
           name = "good.getAllMine",
           query = "SELECT g FROM Good AS g WHERE g.good_report_id = :report ORDER BY g.id DESC"
          ),

    //指定した日報にいいねした件数を取得する(〇件)
    @NamedQuery(
           name = "good.countAllMine",
           query = "SELECT COUNT(g) FROM Good AS g WHERE g.good_report_id = :report"
           ),
    //指定した日報にログイン中の従業員がいいねの件数を取得
    @NamedQuery(
            name = "good.countEmp",
            query = "SELECT COUNT(g) FROM Good AS g WHERE g.good_report_id = :report AND g.good_employee_id = :employee"
            ),

    })


@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数に持つ引数ありコンストラクタを自動生成する(Lombok)
@Entity



public class Good {

    /**
     * id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * いいねをした従業員
     */
    @ManyToOne
    @JoinColumn(name = "good_employee_id", nullable = false)
    private Employee good_employee_id;

    /**
     * いいねをする日報
     */
    @ManyToOne
    @JoinColumn(name = "good_report_id", nullable = false)
    private Report good_report_id;

    /**
     * 登録日時
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;



}
