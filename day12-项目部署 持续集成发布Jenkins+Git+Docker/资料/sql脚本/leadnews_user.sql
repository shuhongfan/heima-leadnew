CREATE DATABASE IF NOT EXISTS leadnews_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadnews_user;
SET NAMES utf8;
/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost:3306
 Source Schema         : leadnews_user

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 24/06/2021 21:59:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ap_user
-- ----------------------------
DROP TABLE IF EXISTS `ap_user`;
CREATE TABLE `ap_user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `salt` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码、通信等加密盐',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码,md5加密',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `sex` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '0 男\r\n            1 女\r\n            2 未知',
  `is_certification` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '0 未\r\n            1 是',
  `is_identity_authentication` tinyint(1) NULL DEFAULT NULL COMMENT '是否身份认证',
  `status` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '0正常\r\n            1锁定',
  `flag` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '0 普通用户\r\n            1 自媒体人\r\n            2 大V',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ap_user
-- ----------------------------
INSERT INTO `ap_user` VALUES (1, 'abc', 'zhangsan', 'abc', '13511223453', NULL, 1, NULL, NULL, 1, 1, '2020-03-19 23:22:07');
INSERT INTO `ap_user` VALUES (2, 'abc', 'lisi', 'abc', '13511223454', '', 1, NULL, NULL, 1, 1, '2020-03-19 23:22:07');
INSERT INTO `ap_user` VALUES (3, 'sdsa', 'wangwu', 'wangwu', '13511223455', NULL, NULL, NULL, NULL, NULL, 1, NULL);
INSERT INTO `ap_user` VALUES (4, '123abc', 'admin', '81e158e10201b6d7aee6e35eaf744796', '13511223456', NULL, 1, NULL, NULL, 1, 1, '2020-03-30 16:36:32');
INSERT INTO `ap_user` VALUES (5, '123abc', 'sunwukong', '81e158e10201b6d7aee6e35eaf744796', '13511223458', NULL, 1, NULL, NULL, 1, 1, '2020-08-01 11:09:57');
INSERT INTO `ap_user` VALUES (6, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for ap_user_fan
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_fan`;
CREATE TABLE `ap_user_fan`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `fans_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '粉丝ID',
  `fans_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '粉丝昵称',
  `level` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '粉丝忠实度\r\n            0 正常\r\n            1 潜力股\r\n            2 勇士\r\n            3 铁杆\r\n            4 老铁',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `is_display` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '是否可见我动态',
  `is_shield_letter` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '是否屏蔽私信',
  `is_shield_comment` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '是否屏蔽评论',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP用户粉丝信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ap_user_fan
-- ----------------------------

-- ----------------------------
-- Table structure for ap_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_follow`;
CREATE TABLE `ap_user_follow`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `follow_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '关注作者ID',
  `follow_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '粉丝昵称',
  `level` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '关注度\r\n            0 偶尔感兴趣\r\n            1 一般\r\n            2 经常\r\n            3 高度',
  `is_notice` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '是否动态通知',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP用户关注信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ap_user_follow
-- ----------------------------

-- ----------------------------
-- Table structure for ap_user_realname
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_realname`;
CREATE TABLE `ap_user_realname`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '账号ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `idno` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源名称',
  `font_image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '正面照片',
  `back_image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '背面照片',
  `hold_image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手持照片',
  `live_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活体照片',
  `status` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '状态\r\n            0 创建中\r\n            1 待审核\r\n            2 审核失败\r\n            9 审核通过',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `submited_time` datetime(0) NULL DEFAULT NULL COMMENT '提交时间',
  `updated_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP实名认证信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ap_user_realname
-- ----------------------------
INSERT INTO `ap_user_realname` VALUES (1, 1, 'zhangsan', '512335455602781278', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', 9, '', '2019-07-30 14:34:28', '2019-07-30 14:34:30', '2019-07-12 06:48:04');
INSERT INTO `ap_user_realname` VALUES (2, 2, 'lisi', '512335455602781279', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', 1, '', '2019-07-11 17:21:18', '2019-07-11 17:21:20', '2019-07-12 06:48:04');
INSERT INTO `ap_user_realname` VALUES (3, 3, 'wangwu6666', '512335455602781276', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', 9, '', '2019-07-11 17:21:18', '2019-07-11 17:21:20', '2019-07-12 06:48:04');
INSERT INTO `ap_user_realname` VALUES (5, 5, 'suwukong', '512335455602781279', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbHSAQlqFAAXIZNzAq9E126.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbF6AR16RAAZB2e1EsOg460.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9bbDeAH2qoAAbD_WiUJfk745.jpg', 'http://161.189.111.227/group1/M00/00/00/rBFwgF9ba9qANVEdAAS25KJlEVE291.jpg', 1, '', '2020-08-01 11:10:31', '2020-08-01 11:10:34', '2020-08-01 11:10:36');

SET FOREIGN_KEY_CHECKS = 1;
