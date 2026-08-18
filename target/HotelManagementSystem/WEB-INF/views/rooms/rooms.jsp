<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html class="light" lang="en">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>Room Search - LuxeStay Management</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "inverse-on-surface": "#f1f0f7",
                        "surface-container-lowest": "#ffffff",
                        "on-primary-fixed-variant": "#264191",
                        "primary-container": "#1e3a8a",
                        "on-background": "#1a1b21",
                        "surface-tint": "#4059aa",
                        "primary-fixed-dim": "#b6c4ff",
                        "on-error": "#ffffff",
                        "background": "#faf8ff",
                        "inverse-surface": "#2f3036",
                        "surface-container-highest": "#e3e1e9",
                        "surface-container": "#eeedf4",
                        "on-tertiary-container": "#4e3d00",
                        "surface-dim": "#dad9e1",
                        "primary-fixed": "#dce1ff",
                        "tertiary-container": "#cca730",
                        "primary": "#00236f",
                        "on-secondary": "#ffffff",
                        "outline-variant": "#c5c5d3",
                        "secondary": "#516072",
                        "secondary-fixed-dim": "#b9c8de",
                        "outline": "#757682",
                        "surface-container-low": "#f4f3fa",
                        "on-primary": "#ffffff",
                        "on-surface-variant": "#444651",
                        "on-tertiary": "#ffffff",
                        "error": "#ba1a1a",
                        "inverse-primary": "#b6c4ff",
                        "tertiary-fixed-dim": "#e9c349",
                        "surface-variant": "#e3e1e9",
                        "secondary-fixed": "#d4e4fa",
                        "on-secondary-fixed-variant": "#39485a",
                        "on-surface": "#1a1b21",
                        "on-secondary-fixed": "#0d1c2d",
                        "tertiary": "#735c00",
                        "on-primary-fixed": "#00164e",
                        "on-secondary-container": "#556477",
                        "surface-bright": "#faf8ff",
                        "on-tertiary-fixed": "#241a00",
                        "on-primary-container": "#90a8ff",
                        "secondary-container": "#d2e1f7",
                        "on-tertiary-fixed-variant": "#574500",
                        "on-error-container": "#93000a",
                        "surface-container-high": "#e9e7ef",
                        "error-container": "#ffdad6",
                        "surface": "#faf8ff",
                        "tertiary-fixed": "#ffe088"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "max-width": "1440px",
                        "md": "1rem",
                        "margin": "2rem",
                        "base": "4px",
                        "lg": "1.5rem",
                        "gutter": "1.5rem",
                        "xl": "2rem",
                        "xs": "0.5rem",
                        "sm": "0.75rem"
                    },
                    "fontFamily": {
                        "caption": ["Inter"],
                        "body-lg": ["Inter"],
                        "headline-md": ["Inter"],
                        "body-md": ["Inter"],
                        "label-md": ["Inter"],
                        "display-lg": ["Inter"],
                        "title-md": ["Inter"],
                        "title-lg": ["Inter"],
                        "headline-lg": ["Inter"]
                    },
                    "fontSize": {
                        "caption": ["12px", { "lineHeight": "1.4", "fontWeight": "400" }],
                        "body-lg": ["16px", { "lineHeight": "1.6", "fontWeight": "400" }],
                        "headline-md": ["24px", { "lineHeight": "1.3", "fontWeight": "600" }],
                        "body-md": ["14px", { "lineHeight": "1.5", "fontWeight": "400" }],
                        "label-md": ["12px", { "lineHeight": "1.2", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "display-lg": ["48px", { "lineHeight": "1.2", "letterSpacing": "-0.02em", "fontWeight": "700" }],
                        "title-md": ["16px", { "lineHeight": "1.4", "fontWeight": "600" }],
                        "title-lg": ["20px", { "lineHeight": "1.4", "fontWeight": "600" }],
                        "headline-lg": ["32px", { "lineHeight": "1.25", "letterSpacing": "-0.01em", "fontWeight": "600" }]
                    }
                }
            }
        }
    </script>
    <style>
        body {
            background-color: theme('colors.surface');
            color: theme('colors.on-surface');
            font-family: 'Inter', sans-serif;
            -webkit-font-smoothing: antialiased;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
    </style>
</head>
<body class="bg-background text-on-background font-body-md min-h-screen flex flex-col pt-16">

<!-- TopNavBar - Full Width Edge-to-Edge matching index.jsp -->
<nav class="bg-surface/95 backdrop-blur-md dark:bg-inverse-surface/95 fixed top-0 left-0 w-full z-50 border-b border-outline-variant/40 shadow-sm">
    <div class="w-full flex justify-between items-center px-6 md:px-12 h-16">
        <div class="flex items-center gap-md">
            <a href="${pageContext.request.contextPath}/" class="text-title-lg font-title-lg font-bold text-primary dark:text-primary-fixed-dim tracking-tight">LuxeStay HMS</a>
        </div>
        <div class="flex-1 max-w-md mx-lg hidden md:block">
            <div class="relative">
                <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
                <input id="keywordSearch" value="<c:out value='${keyword}'/>" onkeydown="if(event.key==='Enter'){loadRoomsFromApi(1);}" class="w-full bg-surface-container-low border border-outline-variant rounded-full py-2 pl-10 pr-4 text-body-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all" placeholder="Search rooms by name or keyword..." type="text">
            </div>
        </div>
        <div class="flex items-center gap-sm">
            <a href="${pageContext.request.contextPath}/" class="hidden sm:inline-flex items-center px-3 py-1.5 text-body-md font-medium text-on-surface-variant hover:text-primary transition-colors">Home</a>
            <a href="${pageContext.request.contextPath}/rooms" class="hidden sm:inline-flex items-center px-3 py-1.5 text-body-md font-bold text-primary border-b-2 border-primary">Find Rooms</a>
            <button aria-label="Notifications" class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-low transition-colors rounded-full flex items-center justify-center h-10 w-10" title="Notifications">
                <span class="material-symbols-outlined" data-icon="notifications">notifications</span>
            </button>
            <button aria-label="Help" class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-low transition-colors rounded-full flex items-center justify-center h-10 w-10" title="Help">
                <span class="material-symbols-outlined" data-icon="help_outline">help_outline</span>
            </button>
            <a href="${pageContext.request.contextPath}/login" aria-label="Account" class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-low transition-colors rounded-full flex items-center justify-center h-10 w-10" title="Account / Login">
                <span class="material-symbols-outlined" data-icon="account_circle">account_circle</span>
            </a>
        </div>
    </div>
</nav>

<!-- Main Content - Full Width Edge-to-Edge with consistent px-6 md:px-12 -->
<main class="flex-grow w-full px-6 md:px-12 pt-6 pb-xl flex flex-col gap-6">
    <!-- Breadcrumb -->
    <nav aria-label="Breadcrumb" class="flex text-body-md text-on-surface-variant">
        <ol class="inline-flex items-center space-x-1 md:space-x-3">
            <li class="inline-flex items-center">
                <a class="inline-flex items-center hover:text-primary transition-colors" href="${pageContext.request.contextPath}/">
                    Home
                </a>
            </li>
            <li aria-current="page">
                <div class="flex items-center">
                    <span class="material-symbols-outlined mx-1 text-sm">chevron_right</span>
                    <span class="text-on-background font-medium">Search Rooms</span>
                </div>
            </li>
        </ol>
    </nav>

    <!-- Search Bar Header -->
    <section class="bg-surface-container-lowest rounded-2xl shadow-sm border border-outline-variant/60 p-lg">
        <h1 class="text-headline-lg font-headline-lg mb-4 text-on-surface">Find Availability</h1>
        <form class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 items-end" id="searchForm" onsubmit="event.preventDefault(); loadRoomsFromApi(1);">
            <div class="space-y-2">
                <label class="text-label-md font-label-md text-on-surface-variant uppercase tracking-wider block">Check-in</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline" data-icon="calendar_today">calendar_today</span>
                    <input id="checkInDate" value="${checkIn}" class="w-full pl-10 pr-3 py-2 bg-surface-container-low border border-outline-variant rounded-xl text-body-md focus:ring-2 focus:ring-primary focus:border-primary outline-none text-on-surface" type="date">
                </div>
            </div>
            <div class="space-y-2">
                <label class="text-label-md font-label-md text-on-surface-variant uppercase tracking-wider block">Check-out</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline" data-icon="calendar_today">calendar_today</span>
                    <input id="checkOutDate" value="${checkOut}" class="w-full pl-10 pr-3 py-2 bg-surface-container-low border border-outline-variant rounded-xl text-body-md focus:ring-2 focus:ring-primary focus:border-primary outline-none text-on-surface" type="date">
                </div>
            </div>
            <div class="space-y-2">
                <label class="text-label-md font-label-md text-on-surface-variant uppercase tracking-wider block">Guests</label>
                <div class="relative">
                    <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline" data-icon="group">group</span>
                    <select id="guestCount" class="w-full pl-10 pr-3 py-2 bg-surface-container-low border border-outline-variant rounded-xl text-body-md focus:ring-2 focus:ring-primary focus:border-primary outline-none text-on-surface appearance-none">
                        <option value="1" ${guests == 1 ? 'selected' : ''}>1 Guest</option>
                        <option value="2" ${guests == 2 ? 'selected' : ''}>2 Guests</option>
                        <option value="3" ${guests == 3 ? 'selected' : ''}>3 Guests</option>
                        <option value="4" ${guests >= 4 ? 'selected' : ''}>4+ Guests</option>
                    </select>
                </div>
            </div>
            <button class="w-full py-2 px-4 bg-primary text-on-primary rounded-xl text-title-md font-title-md hover:bg-primary-container transition-colors shadow-md shadow-primary/20 flex items-center justify-center gap-2 h-[42px] cursor-pointer" type="submit">
                <span class="material-symbols-outlined text-sm" data-icon="search">search</span>
                Search Rooms
            </button>
        </form>
    </section>

    <!-- Main Grid: Filter Sidebar + Room Cards -->
    <div class="flex flex-col lg:flex-row gap-8">
        <!-- Sidebar Filters -->
        <aside class="w-full lg:w-72 flex-shrink-0">
            <div class="bg-surface-container-lowest rounded-2xl shadow-sm border border-outline-variant/60 p-6 sticky top-24">
                <div class="flex justify-between items-center mb-6 pb-2 border-b border-outline-variant/60">
                    <h2 class="text-title-lg font-title-lg font-bold">Filters</h2>
                    <span id="roomCountBadge" class="text-xs bg-primary/10 text-primary font-bold px-2.5 py-0.5 rounded-full">${pageResult.totalItems} rooms</span>
                </div>

                <!-- Filter Section: Room Type -->
                <div class="mb-6">
                    <h3 class="text-label-md font-label-md text-on-surface-variant uppercase mb-3">Room Type</h3>
                    <div class="space-y-2">
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input checked class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-type" value="DELUXE_OCEAN" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Deluxe Ocean View</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input checked class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-type" value="EXEC_SUITE" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Executive Suite</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input checked class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-type" value="PREM_GARDEN" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Premium Garden</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input checked class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-type" value="PRESIDENTIAL" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Presidential Penthouse</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input checked class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-type" value="STD_CITY" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Standard City View</span>
                        </label>
                    </div>
                </div>

                <!-- Filter Section: Price Range -->
                <div class="mb-6">
                    <h3 class="text-label-md font-label-md text-on-surface-variant uppercase mb-3">Price Per Night</h3>
                    <input id="priceSlider" class="w-full h-2 bg-surface-container-highest rounded-lg appearance-none cursor-pointer accent-primary mb-2" max="1000" min="100" type="range" value="${maxPrice}" oninput="document.getElementById('priceDisplay').textContent = '$' + this.value;" onchange="loadRoomsFromApi(1)">
                    <div class="flex justify-between text-caption font-caption text-on-surface-variant">
                        <span>$100</span>
                        <span id="priceDisplay" class="font-bold text-primary">$<fmt:formatNumber value="${maxPrice}" pattern="#,###"/></span>
                        <span>$1,000+</span>
                    </div>
                </div>

                <!-- Filter Section: Amenities -->
                <div class="mb-6">
                    <h3 class="text-label-md font-label-md text-on-surface-variant uppercase mb-3">Amenities</h3>
                    <div class="space-y-2">
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-amenity" value="Ocean View" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Ocean View</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-amenity" value="Balcony" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Balcony</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-amenity" value="Club Access" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Club Access</span>
                        </label>
                        <label class="flex items-center gap-2 text-body-md cursor-pointer group">
                            <input class="rounded border-outline-variant text-primary focus:ring-primary w-4 h-4 filter-amenity" value="Private Jacuzzi" type="checkbox" onchange="loadRoomsFromApi(1)">
                            <span class="group-hover:text-primary transition-colors">Private Jacuzzi</span>
                        </label>
                    </div>
                </div>

                <!-- Explicit Apply Filter Button -->
                <button onclick="loadRoomsFromApi(1)" class="w-full py-2.5 px-4 bg-surface-container-high hover:bg-primary hover:text-on-primary text-on-surface font-title-md rounded-xl transition-all flex items-center justify-center gap-2 border border-outline-variant/60 shadow-sm cursor-pointer">
                    <span class="material-symbols-outlined text-sm">tune</span>
                    Apply Filters
                </button>
            </div>
        </aside>

        <!-- Results Column -->
        <div class="flex-1 flex flex-col gap-6">
            <div id="loadingState" class="hidden flex-col items-center justify-center py-20 bg-surface-container-lowest rounded-2xl border border-outline-variant/60">
                <div class="w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin mb-3"></div>
                <span class="text-body-md text-on-surface-variant">Searching database for available rooms...</span>
            </div>

            <div id="emptyState" class="${empty pageResult.items ? 'flex' : 'hidden'} flex-col items-center justify-center py-20 bg-surface-container-lowest rounded-2xl border border-outline-variant/60 text-center p-6">
                <span class="material-symbols-outlined text-outline text-[48px] mb-2">hotel_class</span>
                <h3 class="text-title-lg font-bold text-on-surface mb-1">No rooms match your filter criteria</h3>
                <p class="text-body-md text-on-surface-variant max-w-md">Try searching with different keywords, adjusting your price range, or clearing some amenity filters.</p>
            </div>

            <!-- Server-rendered Room Cards Grid via JSTL -->
            <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6" id="roomsGrid">
                <c:forEach items="${pageResult.items}" var="room">
                    <article class="bg-surface-container-lowest rounded-2xl shadow-sm border border-outline-variant/60 overflow-hidden hover:shadow-lg transition-all group flex flex-col">
                        <div class="relative h-48 w-full overflow-hidden">
                            <img class="object-cover w-full h-full group-hover:scale-105 transition-transform duration-500" alt="<c:out value='${room.typeName}'/>" src="${not empty room.images ? room.images[0] : 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80'}">
                            <c:choose>
                                <c:when test="${room.availableRoomsCount <= 2}">
                                    <div class="absolute top-4 right-4 bg-tertiary text-on-tertiary px-2.5 py-1 rounded-lg text-caption font-caption font-bold shadow-sm">Only ${room.availableRoomsCount} Left</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="absolute top-4 right-4 bg-primary/90 text-on-primary px-2.5 py-1 rounded-lg text-caption font-caption font-semibold shadow-sm">${room.availableRoomsCount} Available</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="p-5 flex-1 flex flex-col">
                            <div class="flex justify-between items-start mb-2">
                                <h3 class="text-title-lg font-title-lg font-bold text-on-surface line-clamp-1"><c:out value="${room.typeName}"/></h3>
                                <div class="text-right flex-shrink-0 ml-2">
                                    <div class="text-title-lg font-title-lg font-bold text-primary">$<fmt:formatNumber value="${room.basePrice}" pattern="#,##0.00"/></div>
                                    <div class="text-caption font-caption text-on-surface-variant">per night</div>
                                </div>
                            </div>
                            <div class="flex flex-wrap gap-x-3 gap-y-1 text-caption text-on-surface-variant mb-3 border-b border-outline-variant/60 pb-3">
                                <div class="flex items-center gap-1">
                                    <span class="material-symbols-outlined text-sm" data-icon="person">person</span>
                                    Up to ${room.maxAdults} Guests
                                </div>
                                <div class="flex items-center gap-1">
                                    <span class="material-symbols-outlined text-sm" data-icon="king_bed">king_bed</span>
                                    <c:out value="${not empty room.bedType ? room.bedType : '1 King Bed'}"/>
                                </div>
                            </div>
                            <div class="flex flex-wrap items-center gap-1.5 text-caption text-outline mb-5">
                                <c:forEach items="${room.amenities}" var="amenity" begin="0" end="2">
                                    <span class="px-2 py-1 bg-surface-container rounded text-[11px]"><c:out value="${amenity}"/></span>
                                </c:forEach>
                            </div>
                            <div class="mt-auto pt-2 flex gap-2.5">
                                <a href="${pageContext.request.contextPath}/room-details?id=${room.roomTypeId}" class="flex-1 py-2 px-3 border border-outline-variant text-secondary rounded-xl text-body-md font-semibold hover:bg-surface-container-low transition-colors text-center flex items-center justify-center cursor-pointer">View Details</a>
                                <a href="${pageContext.request.contextPath}/login" class="flex-1 py-2 px-3 bg-primary text-on-primary rounded-xl text-body-md font-semibold hover:bg-primary-container transition-colors text-center flex items-center justify-center shadow-sm cursor-pointer">
                                    Book Now
                                </a>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <!-- Pagination Bar -->
            <div id="paginationBar" class="${pageResult.totalItems > 0 ? 'flex' : 'hidden'} flex-col sm:flex-row justify-between items-center bg-surface-container-lowest border border-outline-variant/60 rounded-2xl p-4 gap-4 shadow-sm">
                <div class="text-body-md text-on-surface-variant" id="pageInfoText">
                    Showing ${(pageResult.page - 1) * pageResult.pageSize + 1} to ${(pageResult.page * pageResult.pageSize) > pageResult.totalItems ? pageResult.totalItems : (pageResult.page * pageResult.pageSize)} of ${pageResult.totalItems} rooms
                </div>
                <div class="flex items-center gap-2" id="paginationControls">
                    <!-- Prev Button -->
                    <button onclick="loadRoomsFromApi('${pageResult.page - 1}')" ${pageResult.page <= 1 ? 'disabled' : ''} class="px-3 py-1.5 rounded-lg border border-outline-variant text-body-md font-medium ${pageResult.page <= 1 ? 'opacity-40 cursor-not-allowed' : 'hover:bg-surface-container-low cursor-pointer'} flex items-center gap-1">
                        <span class="material-symbols-outlined text-sm">chevron_left</span> Prev
                    </button>
                    <!-- Numbers -->
                    <c:forEach begin="1" end="${pageResult.totalPages()}" var="pageNum">
                        <button onclick="loadRoomsFromApi('${pageNum}'  )" class="w-9 h-9 rounded-lg font-medium text-body-md flex items-center justify-center transition-colors cursor-pointer ${pageNum == pageResult.page ? 'bg-primary text-on-primary font-bold shadow-sm' : 'border border-outline-variant hover:bg-surface-container-low'}">
                            ${pageNum}
                        </button>
                    </c:forEach>
                    <!-- Next Button -->
                    <button onclick="loadRoomsFromApi('${pageResult.page + 1}')" ${pageResult.page >= pageResult.totalPages() ? 'disabled' : ''} class="px-3 py-1.5 rounded-lg border border-outline-variant text-body-md font-medium ${pageResult.page >= pageResult.totalPages() ? 'opacity-40 cursor-not-allowed' : 'hover:bg-surface-container-low cursor-pointer'} flex items-center gap-1">
                        Next <span class="material-symbols-outlined text-sm">chevron_right</span>
                    </button>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Footer -->
<footer class="w-full py-md px-6 md:px-12 flex flex-col md:flex-row justify-between items-center gap-sm bg-surface-container-low dark:bg-surface-dim border-t border-outline-variant mt-auto">
    <div class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant">
        © 2026 LuxeStay Operational Systems. All Rights Reserved.
    </div>
    <nav class="flex gap-4 text-caption font-caption">
        <a class="text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">Privacy Policy</a>
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">Support Docs</a>
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">Contact Admin</a>
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">System Status</a>
    </nav>
</footer>

<script>
const contextPath = '/HotelManagementSystem';
console.log('Context Path:', contextPath);
   let pageData = {
    items: [],
    page: Number('${pageResult.page}'),
    pageSize: Number('${pageResult.pageSize}'),
    totalItems: Number('${pageResult.totalItems}'),
    totalPages: Number('${pageResult.totalPages()}')
};


    async function loadRoomsFromApi(targetPage = 1) {
        const loading = document.getElementById('loadingState');
        const grid = document.getElementById('roomsGrid');
        const empty = document.getElementById('emptyState');
        const paginationBar = document.getElementById('paginationBar');

        loading.classList.remove('hidden');
        loading.classList.add('flex');
        grid.innerHTML = '';
        empty.classList.add('hidden');
        paginationBar.classList.add('hidden');

        const keyword = (document.getElementById('keywordSearch').value || '').trim();
        const checkIn = document.getElementById('checkInDate').value;
        const checkOut = document.getElementById('checkOutDate').value;
        const guests = document.getElementById('guestCount').value;
        const maxPrice = document.getElementById('priceSlider').value;
        const selectedTypes = Array.from(document.querySelectorAll('.filter-type:checked')).map(cb => cb.value).join(',');
        const selectedAmenities = Array.from(document.querySelectorAll('.filter-amenity:checked')).map(cb => cb.value).join(',');

        try {
            let params = new URLSearchParams();
            params.append('page', targetPage);
            params.append('pageSize', 6);
            if (keyword) params.append('keyword', keyword);
            if (checkIn) params.append('checkIn', checkIn);
            if (checkOut) params.append('checkOut', checkOut);
            if (guests) params.append('guests', guests);
            if (maxPrice) params.append('maxPrice', maxPrice);
            if (selectedTypes) params.append('types', selectedTypes);
            if (selectedAmenities) params.append('amenities', selectedAmenities);

            const url = '/HotelManagementSystem/api/rooms?' + params.toString();
            const response = await fetch(url);
            if (!response.ok) throw new Error('API query failed');
            
            pageData = await response.json();
            renderRoomCards(pageData.items);
            renderPagination();
        } catch (error) {
            console.error('Error querying rooms from database:', error);
            grid.innerHTML = '<div class="col-span-3 text-center text-error py-8">Unable to query rooms from database. Please check connection.</div>';
        } finally {
            loading.classList.remove('flex');
            loading.classList.add('hidden');
        }
    }

    function renderRoomCards(rooms) {
    const grid = document.getElementById('roomsGrid');
    const empty = document.getElementById('emptyState');
    document.getElementById('roomCountBadge').textContent = `${pageData.totalItems} rooms`;

    if (!rooms || rooms.length === 0) {
        grid.innerHTML = '';
        empty.classList.remove('hidden');
        empty.classList.add('flex');
        return;
    }

    empty.classList.add('hidden');
    empty.classList.remove('flex');

    grid.innerHTML = rooms.map(room => {
        const img = (room.images && room.images.length > 0) ? room.images[0] : 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80';
        const amenitiesHtml = (room.amenities || []).slice(0, 3).map(a => `<span class="px-2 py-1 bg-surface-container rounded text-[11px]">${a}</span>`).join('');
        const leftBadge = room.availableRoomsCount <= 2 ?
            `<div class="absolute top-4 right-4 bg-tertiary text-on-tertiary px-2.5 py-1 rounded-lg text-caption font-caption font-bold shadow-sm">Only \${room.availableRoomsCount} Left</div>` :
            `<div class="absolute top-4 right-4 bg-primary/90 text-on-primary px-2.5 py-1 rounded-lg text-caption font-caption font-semibold shadow-sm">\${room.availableRoomsCount} Available</div>`;

        return `
            <article class="bg-surface-container-lowest rounded-2xl shadow-sm border border-outline-variant/60 overflow-hidden hover:shadow-lg transition-all group flex flex-col">
                <div class="relative h-48 w-full overflow-hidden">
                    <img class="object-cover w-full h-full group-hover:scale-105 transition-transform duration-500" alt="\${room.typeName}" src="\${img}">
                    \${leftBadge}
                </div>
                <div class="p-5 flex-1 flex flex-col">
                    <div class="flex justify-between items-start mb-2">
                        <h3 class="text-title-lg font-title-lg font-bold text-on-surface line-clamp-1">\${room.typeName}</h3>
                        <div class="text-right flex-shrink-0 ml-2">
                            <div class="text-title-lg font-title-lg font-bold text-primary">$\${room.basePrice}</div>
                            <div class="text-caption font-caption text-on-surface-variant">per night</div>
                        </div>
                    </div>
                    <div class="flex flex-wrap gap-x-3 gap-y-1 text-caption text-on-surface-variant mb-3 border-b border-outline-variant/60 pb-3">
                        <div class="flex items-center gap-1">
                            <span class="material-symbols-outlined text-sm" data-icon="person">person</span>
                            Up to \${room.maxAdults} Guests
                        </div>
                        <div class="flex items-center gap-1">
                            <span class="material-symbols-outlined text-sm" data-icon="king_bed">king_bed</span>
                            \${room.bedType || '1 King Bed'}
                        </div>
                    </div>
                    <div class="flex flex-wrap items-center gap-1.5 text-caption text-outline mb-5">
                        \${amenitiesHtml}
                    </div>
                    <div class="mt-auto pt-2 flex gap-2.5">
                        <a href="/HotelManagementSystem/room-details?id=\${room.roomTypeId}" class="flex-1 py-2 px-3 border border-outline-variant text-secondary rounded-xl text-body-md font-semibold hover:bg-surface-container-low transition-colors text-center flex items-center justify-center cursor-pointer">View Details</a>
                        <a href="/HotelManagementSystem/login" class="flex-1 py-2 px-3 bg-primary text-on-primary rounded-xl text-body-md font-semibold hover:bg-primary-container transition-colors text-center flex items-center justify-center shadow-sm cursor-pointer">
                            Book Now
                        </a>
                    </div>
                </div>
            </article>
        `;
    }).join('');
}


    function renderPagination() {
        const paginationBar = document.getElementById('paginationBar');
        const controls = document.getElementById('paginationControls');
        const pageInfoText = document.getElementById('pageInfoText');

        if (pageData.totalItems === 0) {
            paginationBar.classList.add('hidden');
            return;
        }

        paginationBar.classList.remove('hidden');
        paginationBar.classList.add('flex');

        const from = (pageData.page - 1) * pageData.pageSize + 1;
        const to = Math.min(pageData.page * pageData.pageSize, pageData.totalItems);
        pageInfoText.textContent = `Showing ${from} to ${to} of ${pageData.totalItems} rooms`;

        let html = '';

        const prevDisabled = pageData.page <= 1;
        html += `
            <button onclick="loadRoomsFromApi(${pageData.page - 1})" ${prevDisabled ? 'disabled' : ''} class="px-3 py-1.5 rounded-lg border border-outline-variant text-body-md font-medium ${prevDisabled ? 'opacity-40 cursor-not-allowed' : 'hover:bg-surface-container-low cursor-pointer'} flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">chevron_left</span> Prev
            </button>
        `;

        for (let i = 1; i <= pageData.totalPages; i++) {
            const active = i === pageData.page;
            html += `
                <button onclick="loadRoomsFromApi(${i})" class="w-9 h-9 rounded-lg font-medium text-body-md flex items-center justify-center transition-colors cursor-pointer ${active ? 'bg-primary text-on-primary font-bold shadow-sm' : 'border border-outline-variant hover:bg-surface-container-low'}">
                    ${i}
                </button>
            `;
        }

        const nextDisabled = pageData.page >= pageData.totalPages;
        html += `
            <button onclick="loadRoomsFromApi(${pageData.page + 1})" ${nextDisabled ? 'disabled' : ''} class="px-3 py-1.5 rounded-lg border border-outline-variant text-body-md font-medium ${nextDisabled ? 'opacity-40 cursor-not-allowed' : 'hover:bg-surface-container-low cursor-pointer'} flex items-center gap-1">
                Next <span class="material-symbols-outlined text-sm">chevron_right</span>
            </button>
        `;

        controls.innerHTML = html;
    }
</script>

</body>
</html>
