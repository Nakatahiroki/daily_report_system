package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.GoodConverter;
import actions.views.GoodView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Good;

/**
 * いいねテーブルの操作に関わる処理を行うクラス
 *
 */

public class GoodService extends ServiceBase {

        /**
         * 対象の日報データを、指定されたページ数の一覧画面に表示する分を取得しReportViewのリストで返却する
         * @param report 日報
         * @param page ページ数
         * @return 一覧画面に表示するデータのリスト
         */
        public List<GoodView> getMinePerPage(ReportView report, int page){

            List<Good> goods = em.createNamedQuery("good.getAllMine", Good.class)
                    .setParameter("report", ReportConverter.toModel(report))
                    .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                    .setMaxResults(JpaConst.ROW_PER_PAGE)
                    .getResultList();
            return GoodConverter.toViewList(goods);
        }


        /**
         * 指定されたページ数の一覧画面に表示するいいねデータを取得し、GoodViewのリストで返却する
         * @param page ページ数
         * @return 一覧画面に表示するデータのリスト
         */
        public List<GoodView> getAllPerPage(int page){

            List<Good> goods = em.createNamedQuery("good.getAll", Good.class)
                    .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                    .setMaxResults(JpaConst.ROW_PER_PAGE)
                    .getResultList();
            return GoodConverter. toViewList(goods);
        }



        /**
         * 対象の日報データのいいね件数を取得
         *  @param reports
         *  @return いいねデータの件数
         */
        public long countAllMine(ReportView report) {

            long count =(long) em.createNamedQuery("good.countAllMine", Long.class)
                    .setParameter("report", ReportConverter.toModel(report))
                    .getSingleResult();

            return count;

        }

    /**
     * いいね押下時に登録内容を元にデータを1件作成し、いいねテーブルに登録する
     * @param gv 日報の登録内容
     */
    public void create(GoodView gv){
            LocalDateTime ldt = LocalDateTime.now();
            gv.setCreatedAt(ldt);
            gv.setUpdatedAt(ldt);
            createInternal(gv);
        }




    /**
     * idを条件に取得した従業員データをGoodViewのインスタンスで返却する
     */
    public GoodView findOne(int id) {
        Good g = findOneInternal(id);
        return GoodConverter.toView(g);
    }

    /**
    * idを条件に従業員データ1件を取得し、Employeeのインスタンスで返却する
    * @param id
    * @return 取得データのインスタンス
    */
    private Good findOneInternal(int id) {
        Good g = em.find(Good.class, id);

        return g;

    }


    /**
     * 指定の日報とログインしている従業員の2つを条件にデータを取得する
     * @param employee
     * @param report
     * @return 日報データの件数
     */
    public long getGoodCount(EmployeeView employee, ReportView report) {

        long count =(long) em.createNamedQuery("good.countEmp", Long.class)
                .setParameter("report", ReportConverter.toModel(report))
                .setParameter("employee", EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }
































    /**
     * いいねデータを1件登録する
     * @param gv 日報データ
     */
    private void createInternal(GoodView gv) {

        em.getTransaction().begin();
        em.persist(GoodConverter.toModel(gv));
        em.getTransaction().commit();
    }




    /**
     * いいねテーブルのデータの件数を取得し、返却する
     * @return データの件数
     */
    public long countAll() {
        long goods_count = (long) em.createNamedQuery("good.Count", Long.class)
                .getSingleResult();
        return goods_count;
    }


}























