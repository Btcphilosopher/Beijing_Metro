# 北京地铁 (Beijing Metro) 智能出行 App - 后端部署与 API 文档

本说明文档包含 **北京地铁** 微服务后端的部署指南、数据库初始化方案、云原生编排以及 REST API 接口详细规格。

---

## 1. 架构组件概述

系统由以下核心模块构成：
1. **Android 客户端 (Frontend):** 采用 Jetpack Compose + MVVM 架构编写。
2. **Spring Boot 服务端 (Backend):** 主业务引擎，提供 Dijkstra/BFS 路径算法和交易验证。
3. **PostgreSQL + PostGIS (Database):** 地理空间数据库，提供高效站点地理坐标检索及换乘拓扑存储。
4. **Redis Cache (Cache):** 缓存运营广播公告及高频次换乘计算路由，减轻数据库负载。

---

## 2. 数据库部署 (Postgres + PostGIS)

北京地铁涉及大量的地理坐标检索（如：寻找距当前位置 500 米内的最近站点）。数据库必须安装并启用 `PostGIS` 扩展。

### SQL 初始化
使用 `/backend/database/schema.sql` 完成数据表创建与初始种子导入。主要结构：
- `users`: 系统用户表。
- `metro_lines`: 运营线路信息（包含官方色码）。
- `metro_stations`: 坐标（`GEOMETRY(Point, 4326)`）、出入口及公共洗手间无障碍配置。
- `line_stations`: 顺序站点映射，定义物理线路走向。
- `transfer_connections`: 换乘联通权重及时间延迟。

---

## 3. 容器化部署方案

### 3.1 本地 Docker Compose 一键启动
在本地开发及测试环境下，推荐使用 `docker-compose.yml` 一键运行所有基础设施：

```bash
# 导航到 docker 目录
cd backend/docker

# 构建并启动后台所有微服务
docker-compose up -d --build
```
启动成功后：
- Spring Boot REST 端点：`http://localhost:8080/`
- PostgreSQL 数据源：`localhost:5432` （用户：`bjmetro_admin`，密码：`SecretPassword123`）
- Redis 缓存：`localhost:6379`

### 3.2 生产环境 Kubernetes 编排
生产级部署采用 K8s 原生声明配置（见 `/backend/k8s/deployment.yaml`）：
- **滚动更新 (Rolling Update):** 副本数设为 3，支持无缝停机升级。
- **健康探测 (Probes):** 内置 Liveness 和 Readiness 探针。
- **自动扩缩 (HPA):** 当 CPU 负载达到 75% 时，自动在 2 至 10 个 Pod 间自适应伸缩。

部署指令：
```bash
kubectl apply -f backend/k8s/deployment.yaml
```

---

## 4. REST API 接口文档 (Swagger JSON 规范)

以下为核心路由控制器的 REST API 端点定义。

### 4.1 智能路径规划
计算两站之间最优换乘路径、票价、用时、途径站。

* **URL:** `/api/route/plan`
* **Method:** `GET`
* **Query 参数:**
  * `startStationId` (string, required) - 起点站 ID (如: `fuxingmen`)
  * `endStationId` (string, required) - 终点站 ID (如: `xizhimen`)
* **成功响应 (200 OK):**
```json
{
  "success": true,
  "data": {
    "path": ["fuxingmen", "xizhimen"],
    "totalTimeMinutes": 7,
    "priceRmb": 3.0,
    "transferCount": 0,
    "description": "乘车途径 1 站，预计耗时 7 分钟，票价 3.0 元，换乘 0 次。"
  }
}
```

---

### 4.2 获取运营公告与列车延误广播
用户端首页展示的实时运营警告。

* **URL:** `/api/announcements`
* **Method:** `GET`
* **成功响应 (200 OK):**
```json
[
  {
    "id": 1,
    "contentZh": "北京地铁全线运营正常。今日大兴机场线、首都机场线运行平稳，发车间隔正常。",
    "contentEn": "All Beijing Metro lines are operating normally today. Capital Airport Express and Daxing Airport Express are running smoothly.",
    "time": "2026-07-08 06:00",
    "isUrgent": false
  }
}
```

---

### 4.3 闸机乘车码扣费核销
模拟线下智能闸机对乘客 APP 生成的电子票务条码进行扫描、安全性校验与账户余额划扣。

* **URL:** `/api/tickets/scan`
* **Method:** `POST`
* **Payload 请求体:**
```json
{
  "qrCode": "BJMETRO-RIDE-7cfc1d8a-9214-4fb5-90ae-db7a7df9eb89",
  "fare": 5.0
}
```
* **成功响应 (200 OK):**
```json
{
  "authorized": true,
  "gateState": "OPEN",
  "deductedFare": 5.0,
  "transactionTimestamp": 1783478400000
}
```

---

### 4.4 调度发布紧急限流公告 (管理后台接口)
地铁运营中心紧急通知、施工绕行或限流警示发布。

* **URL:** `/api/admin/announcements`
* **Method:** `POST`
* **Payload 请求体:**
```json
{
  "contentZh": "地铁2号线西直门站由于客流过大，目前实施临时限流措施，请乘客听从指挥。",
  "contentEn": "Xizhimen Station (Line 2) is experiencing high passenger volumes. Temporary crowd control is in place.",
  "isUrgent": true
}
```
* **成功响应 (200 OK):**
```json
{
  "success": true,
  "message": "Operational bulletin published successfully!",
  "announcement": {
    "id": 2,
    "contentZh": "地铁2号线西直门站由于客流过大...",
    "contentEn": "Xizhimen Station (Line 2) is...",
    "time": "2026-07-08 10:15",
    "isUrgent": true
  }
}
```
