<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Housekeeping - Dọn dẹp Buồng phòng</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .alert-success { color: #155724; padding: 10px; border: 1px solid #c3e6cb; background: #d4edda; margin-bottom: 20px;}
        .alert-error { color: #721c24; padding: 10px; border: 1px solid #f5c6cb; background: #f8d7da; margin-bottom: 20px;}
        table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #f4f4f4; }
        .badge { padding: 4px 8px; border-radius: 4px; color: white; font-weight: bold; font-size: 12px; }
        .priority-URGENT { background-color: #dc3545; }
        .priority-HIGH { background-color: #fd7e14; }
        .priority-NORMAL { background-color: #17a2b8; }
        .btn { padding: 6px 12px; cursor: pointer; border: none; border-radius: 4px;}
        .btn-accept { background-color: #0d6efd; color: white; }
        .btn-start { background-color: #ffc107; color: black; }
        .btn-complete { background-color: #198754; color: white; }
    </style>
</head>
<body>

    <h2>Quản lý Buồng phòng (Housekeeping)</h2>

    <c:if test="${not empty success}">
        <div class="alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <hr/>

    <!-- Danh sách 1: Phòng cần dọn (PENDING) -->
    <h3>Danh sách phòng chờ dọn dẹp</h3>
    <table>
        <thead>
            <tr>
                <th>Mã Task</th>
                <th>Phòng</th>
                <th>Loại hình dọn</th>
                <th>Độ ưu tiên</th>
                <th>Thời gian hẹn</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty pendingTasks}">
                    <tr><td colspan="6" style="text-align: center;">Hiện không có phòng nào cần dọn.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="task" items="${pendingTasks}">
                        <tr>
                            <td>#${task.taskId()}</td>
                            <td><strong>Phòng ${task.roomNumber()}</strong></td>
                            <td>${task.taskType()}</td>
                            <td><span class="badge priority-${task.priorityCode()}">${task.priorityCode()}</span></td>
                            <td>${task.scheduledAt() != null ? task.scheduledAt() : 'Thực hiện ngay'}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/staff/housekeeping/accept" method="post">
                                    <input type="hidden" name="taskId" value="${task.taskId()}">
                                    <button type="submit" class="btn btn-accept">Nhận việc</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <!-- Danh sách 2: Phòng tôi đang dọn (ASSIGNED & IN_PROGRESS) -->
    <h3>Công việc dọn dẹp của tôi</h3>
    <table>
        <thead>
            <tr>
                <th>Mã Task</th>
                <th>Phòng</th>
                <th>Loại hình dọn</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty myTasks}">
                    <tr><td colspan="5" style="text-align: center;">Bạn chưa nhận nhiệm vụ dọn dẹp nào.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="myTask" items="${myTasks}">
                        <tr>
                            <td>#${myTask.taskId()}</td>
                            <td><strong>Phòng ${myTask.roomNumber()}</strong></td>
                            <td>${myTask.taskType()}</td>
                            <td>
                                <c:if test="${myTask.statusCode() == 'ASSIGNED'}">Đã tiếp nhận</c:if>
                                <c:if test="${myTask.statusCode() == 'IN_PROGRESS'}"><strong>Đang dọn...</strong></c:if>
                            </td>
                            <td>
                                <c:if test="${myTask.statusCode() == 'ASSIGNED'}">
                                    <form action="${pageContext.request.contextPath}/staff/housekeeping/start" method="post">
                                        <input type="hidden" name="taskId" value="${myTask.taskId()}">
                                        <button type="submit" class="btn btn-start">Bắt đầu dọn</button>
                                    </form>
                                </c:if>

                                <c:if test="${myTask.statusCode() == 'IN_PROGRESS'}">
                                    <form action="${pageContext.request.contextPath}/staff/housekeeping/complete" method="post">
                                        <input type="hidden" name="taskId" value="${myTask.taskId()}">
                                        <button type="submit" class="btn btn-complete">Hoàn tất dọn dẹp</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

</body>
</html>