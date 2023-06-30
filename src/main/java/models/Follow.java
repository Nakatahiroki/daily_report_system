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
 * フォローデータのDTOモデル
 *
 *
 */
@Table(name = "follows")
@NamedQueries({


    //ログイン中の従業員がフォローした日報作成者の日報データ
   @NamedQuery(
           name = "follow.getAllMine",
           query = "SELECT r FROM Report AS r, Follow AS f WHERE r.employee = f.followed_employee_id AND f.follow_employee_id =:employee ORDER BY r.id DESC"
           ),

    //ログイン中の従業員がフォローした日報作成者の日報の総件数
    @NamedQuery(
            name = "follow.countEmp",
            query = "SELECT COUNT(r) FROM Report AS r, Follow AS f WHERE r.employee = f.followed_employee_id AND f.follow_employee_id = :employee"
            ),

    //ログイン中の従業員が指定の従業員をフォローした件数
    @NamedQuery(
            name = "follow.count",
            query = "SELECT COUNT(f) FROM Follow AS f WHERE f.followed_employee_id = :followed_employee AND f.follow_employee_id =:follow_employee"
            ),


})

@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数に持つ引数ありコンストラクタを自動生成する(Lombok)
@Entity


public class Follow {

    /**
     * id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * フォローする従業員
     */
    @ManyToOne
    @JoinColumn(name = "follow_employee_id", nullable = false)
    private Employee follow_employee_id;

    /**
     * フォローされた従業員
     */
    @ManyToOne
    @JoinColumn(name = "followed_employee_id", nullable = false)
    private Employee followed_employee_id;

    /**
     * 登録日時
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = "updatede_at", nullable = false)
    private LocalDateTime updatedAt;
}
