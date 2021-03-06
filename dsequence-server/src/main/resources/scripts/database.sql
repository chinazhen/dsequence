DROP TABLE IF EXISTS `T_SEQUENCE_INFO`;
CREATE TABLE `T_SEQUENCE_INFO` (
  `ID` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `APP_NAME` varchar(32) NOT NULL COMMENT '应用名称',
  `SEQ_NAME` varchar(32) NOT NULL COMMENT '序列号名称',
  `LAST_SEQ` bigint(19) NOT NULL DEFAULT '0' COMMENT '上次使用sequence',
  `NEXT_SEQ` bigint(19) NOT NULL DEFAULT '0' COMMENT '下次使用sequence',
  `MAX_SEQ` bigint(19) NOT NULL DEFAULT '0' COMMENT '允许最大sequence',
  `CREATED_AT` timestamp NOT NULL COMMENT '创建时间',
  `CREATED_BY` varchar(32) NOT NULL COMMENT '创建人',
  `UPDATED_AT` timestamp NOT NULL COMMENT '最后更新时间',
  `UPDATED_BY` varchar(32) NOT NULL COMMENT '最后更新人',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDX_NAME` (`APP_NAME`,`SEQ_NAME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;