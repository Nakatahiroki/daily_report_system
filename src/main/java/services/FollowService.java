package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.FollowConverter;
import actions.views.FollowView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Report;

/**
 * フォローテーブルの操作に関わる処理を行うクラス
 *
 *
 */

public class FollowService extends ServiceBase {

    /**
     * フォローする押下時にログイン中の従業員、日報の作成者、日時のデータを1件作成し、
     * フォローテーブルに登録する。
     * @param ev  画面から入力された従業員の登録内容
     * @param pepper pepper文字列
     * @return バリデーションや登録処理中に発生したエラーのリスト
     */
    public void create(FollowView fv){


        //登録日時、更新日時は現在時刻を設定する
        LocalDateTime ldt = LocalDateTime.now();
        fv.setCreatedAt(ldt);
        fv.setUpdatedAt(ldt);
        createInternal(fv);
    }

    /**
     * フォローデータを1件登録する
     * @param fv フォローデータ
     */
    private void createInternal(FollowView fv) {

        em.getTransaction().begin();
        em.persist(FollowConverter.toModel(fv));
        em.getTransaction().commit();
    }

    /**
     * ログイン中の従業員がフォローした日報作成者のデータをページ指定で取得する
     */
    public List<ReportView> getMinePerPage(EmployeeView employee, int page){

        List<Report> reports = em.createNamedQuery("follow.getAllMine", Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }




    /**
     * ログイン中の従業員がフォローした日報作成者の日報の件数を取得する
     * @param employee
     * @return 日報データの件数
     */

    public long getFollowCount(EmployeeView employee) {

        long count =(long) em.createNamedQuery("follow.countEmp", Long.class)
                .setParameter("employee", EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    /**
     * 指定の従業員をログイン中の従業員がフォローした件数を取得する
     * @param employee
     * @param report
     * @return フォローデータの件数
     */
    public long followCount(EmployeeView employee,EmployeeView employee02) {

        long count =(long) em.createNamedQuery("follow.count", Long.class)
                .setParameter("followed_employee", EmployeeConverter.toModel(employee))
                .setParameter("follow_employee", EmployeeConverter.toModel(employee02))
                .getSingleResult();

        return count;
    }

}


