<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html class="light" lang="en">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title><c:out value="${not empty room ? room.typeName : 'Room Details'}"/> - LuxeStay Management</title>
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
        .no-scrollbar::-webkit-scrollbar { display: none; }
        .no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
    </style>
</head>
<body class="bg-background text-on-background font-body-md min-h-screen flex flex-col pt-16">

<!-- TopNavBar - Full Width -->
<nav class="bg-surface/95 backdrop-blur-md dark:bg-inverse-surface/95 fixed top-0 left-0 w-full z-40 border-b border-outline-variant/40 shadow-sm">
    <div class="w-full flex justify-between items-center px-6 md:px-12 h-16">
        <div class="flex items-center gap-md">
            <a href="${pageContext.request.contextPath}/" class="text-title-lg font-title-lg font-bold text-primary dark:text-primary-fixed-dim tracking-tight">LuxeStay HMS</a>
        </div>
        <div class="flex-1 max-w-md mx-lg hidden md:block">
            <div class="relative">
                <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
                <input class="w-full bg-surface-container-low border border-outline-variant rounded-full py-2 pl-10 pr-4 text-body-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all" placeholder="Search..." type="text">
            </div>
        </div>
        <div class="flex items-center gap-sm">
            <a href="${pageContext.request.contextPath}/" class="hidden sm:inline-flex items-center px-3 py-1.5 text-body-md font-medium text-on-surface-variant hover:text-primary transition-colors">Home</a>
            <a href="${pageContext.request.contextPath}/rooms" class="hidden sm:inline-flex items-center px-3 py-1.5 text-body-md font-medium text-on-surface-variant hover:text-primary transition-colors">Find Rooms</a>
            <button class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-low transition-colors rounded-full flex items-center justify-center h-10 w-10" title="Notifications">
                <span class="material-symbols-outlined" data-icon="notifications">notifications</span>
            </button>
            <button class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-low transition-colors rounded-full flex items-center justify-center h-10 w-10" title="Help">
                <span class="material-symbols-outlined" data-icon="help_outline">help_outline</span>
            </button>
            <a href="${pageContext.request.contextPath}/login" class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container-low transition-colors rounded-full flex items-center justify-center h-10 w-10" title="Account / Login">
                <span class="material-symbols-outlined" data-icon="account_circle">account_circle</span>
            </a>
        </div>
    </div>
</nav>

<!-- Main Content -->
<main class="flex-1 w-full px-6 md:px-12 pt-6 pb-xl flex flex-col gap-lg">
    <!-- Breadcrumb -->
    <nav aria-label="Breadcrumb" class="flex text-body-md text-on-surface-variant">
        <ol class="inline-flex items-center space-x-1 md:space-x-3">
            <li class="inline-flex items-center">
                <a class="inline-flex items-center hover:text-primary transition-colors" href="${pageContext.request.contextPath}/">
                    Home
                </a>
            </li>
            <li>
                <div class="flex items-center">
                    <span class="material-symbols-outlined mx-1 text-sm">chevron_right</span>
                    <a class="hover:text-primary transition-colors" href="${pageContext.request.contextPath}/rooms">Search</a>
                </div>
            </li>
            <li aria-current="page">
                <div class="flex items-center">
                    <span class="material-symbols-outlined mx-1 text-sm">chevron_right</span>
                    <span class="text-on-background font-medium"><c:out value="${room.typeName}"/></span>
                </div>
            </li>
        </ol>
    </nav>

    <c:choose>
        <c:when test="${empty room}">
            <div class="text-error text-center py-20 bg-surface-container-lowest rounded-2xl border border-outline-variant">
                <span class="material-symbols-outlined text-[48px] mb-2">error</span>
                <h3 class="text-title-lg font-bold">Room not found</h3>
                <p class="text-body-md text-on-surface-variant mt-1">Please return to <a href="${pageContext.request.contextPath}/rooms" class="text-primary underline">Search</a> and select a valid room.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="grid grid-cols-1 lg:grid-cols-12 gap-gutter">
                <!-- Left Column: Gallery & Details -->
                <div class="lg:col-span-8 flex flex-col gap-xl">
                    <!-- Gallery Bento Grid with Click to Open Gallery Modal -->
                    <div class="relative rounded-2xl overflow-hidden shadow-sm select-none">
                        <div class="grid grid-cols-4 grid-rows-2 gap-sm h-[400px] md:h-[500px]" id="galleryContainer">
                            <div onclick="openGallery(0)" class="col-span-4 md:col-span-2 row-span-2 relative group cursor-pointer overflow-hidden">
                                <img alt="Main Room View" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="${not empty room.images ? room.images[0] : 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1600&q=85'}">
                                <div class="absolute inset-0 bg-black/10 group-hover:bg-transparent transition-colors duration-300"></div>
                            </div>
                            <div onclick="openGallery(1)" class="hidden md:block col-span-2 row-span-1 relative group cursor-pointer overflow-hidden">
                                <img alt="Bathroom View" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="${room.images.size() > 1 ? room.images[1] : room.images[0]}">
                                <div class="absolute inset-0 bg-black/10 group-hover:bg-transparent transition-colors duration-300"></div>
                            </div>
                            <div onclick="openGallery(2)" class="hidden md:block col-span-1 row-span-1 relative group cursor-pointer overflow-hidden">
                                <img alt="Detail 1" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="${room.images.size() > 2 ? room.images[2] : room.images[0]}">
                                <div class="absolute inset-0 bg-black/10 group-hover:bg-transparent transition-colors duration-300"></div>
                            </div>
                            <div onclick="openGallery(3)" class="hidden md:block col-span-1 row-span-1 relative group cursor-pointer overflow-hidden">
                                <img alt="Detail 2" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" src="${room.images.size() > 3 ? room.images[3] : room.images[0]}">
                                <div class="absolute inset-0 bg-black/20 group-hover:bg-transparent transition-colors duration-300"></div>
                            </div>
                        </div>
                        <!-- Prominent Show All Photos Button at Bottom Right -->
                        <button onclick="openGallery(0)" class="absolute bottom-4 right-4 bg-surface/95 hover:bg-surface text-on-surface hover:text-primary px-4 py-2 rounded-xl text-body-md font-bold shadow-lg border border-outline-variant/60 flex items-center gap-2 backdrop-blur-md transition-all hover:scale-105 z-10 cursor-pointer">
                            <span class="material-symbols-outlined text-[18px]">grid_view</span>
                            <span>Show all ${room.images.size()} photos</span>
                        </button>
                    </div>

                    <!-- Header Info -->
                    <div class="flex flex-col gap-sm">
                        <div class="flex justify-between items-start">
                            <div>
                                <h1 class="text-headline-lg font-headline-lg text-on-background mb-2"><c:out value="${room.typeName}"/></h1>
                                <p class="text-title-md font-title-md text-on-surface-variant flex items-center gap-2">
                                    <span class="material-symbols-outlined text-tertiary-container text-sm" data-weight="fill">star</span>
                                    <span class="text-on-background font-bold">4.9</span> (128 reviews) · 
                                    <span class="material-symbols-outlined text-sm">location_on</span> <span>${room.availableRoomsCount} Rooms Available</span>
                                </p>
                            </div>
                            <button class="w-10 h-10 rounded-full border border-outline-variant flex items-center justify-center text-on-surface-variant hover:text-error hover:border-error hover:bg-error-container transition-all" title="Save to wishlist">
                                <span class="material-symbols-outlined">favorite_border</span>
                            </button>
                        </div>
                        <div class="flex flex-wrap gap-md text-body-md text-on-surface-variant border-b border-outline-variant pb-md">
                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-sm">person</span> Up to ${room.maxAdults} Guests</span>
                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-sm">bed</span> <c:out value="${not empty room.bedType ? room.bedType : '1 King Bed'}"/></span>
                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-sm">square_foot</span> ${not empty room.roomSizeM2 ? room.roomSizeM2 : 42} m²</span>
                            <span class="flex items-center gap-1"><span class="material-symbols-outlined text-sm">balcony</span> City / Ocean View</span>
                        </div>
                    </div>

                    <!-- Description -->
                    <section>
                        <h2 class="text-title-lg font-title-lg text-on-background mb-md font-bold">About this room</h2>
                        <p class="text-body-lg font-body-lg text-on-surface-variant leading-relaxed">
                            <c:out value="${not empty room.description ? room.description : 'Experience ultimate luxury and comfort at LuxeStay Grand Resort.'}"/>
                        </p>
                    </section>

                    <!-- Amenities -->
                    <section>
                        <h2 class="text-title-lg font-title-lg text-on-background mb-md font-bold">What this room offers</h2>
                        <div class="grid grid-cols-2 gap-y-sm gap-x-lg text-body-lg text-on-surface-variant">
                            <c:forEach items="${room.amenities}" var="amenity">
                                <div class="flex items-center gap-3">
                                    <span class="material-symbols-outlined text-primary">check_circle</span>
                                    <c:out value="${amenity}"/>
                                </div>
                            </c:forEach>
                        </div>
                    </section>
                </div>

                <!-- Right Column: Booking Widget -->
                <div class="lg:col-span-4 relative">
                    <div class="sticky top-[calc(64px+1.5rem)] bg-surface-container-lowest border border-outline-variant rounded-2xl p-lg shadow-sm flex flex-col gap-md">
                        <div class="flex justify-between items-end">
                            <div>
                                <span class="text-headline-md font-headline-md text-on-background font-bold">$<fmt:formatNumber value="${room.basePrice}" pattern="#,##0.00"/></span>
                                <span class="text-body-md text-on-surface-variant">/ night</span>
                            </div>
                            <div class="text-right">
                                <span class="text-title-md font-title-md flex items-center justify-end gap-1"><span class="material-symbols-outlined text-tertiary-container text-sm" data-weight="fill">star</span> 4.9</span>
                                <span class="text-caption text-on-surface-variant hover:underline cursor-pointer">128 reviews</span>
                            </div>
                        </div>
                        <div class="border border-outline-variant rounded-xl overflow-hidden flex flex-col">
                            <div class="flex border-b border-outline-variant">
                                <div class="flex-1 p-3 border-r border-outline-variant cursor-pointer hover:bg-surface-container-low transition-colors">
                                    <div class="text-caption font-label-md text-on-surface-variant uppercase tracking-wide">Check-in</div>
                                    <div class="text-body-md text-on-background font-medium">Tomorrow</div>
                                </div>
                                <div class="flex-1 p-3 cursor-pointer hover:bg-surface-container-low transition-colors">
                                    <div class="text-caption font-label-md text-on-surface-variant uppercase tracking-wide">Check-out</div>
                                    <div class="text-body-md text-on-background font-medium">+4 Days</div>
                                </div>
                            </div>
                            <div class="p-3 cursor-pointer hover:bg-surface-container-low transition-colors flex justify-between items-center">
                                <div>
                                    <div class="text-caption font-label-md text-on-surface-variant uppercase tracking-wide">Guests</div>
                                    <div class="text-body-md text-on-background font-medium">${room.maxAdults} Adults</div>
                                </div>
                                <span class="material-symbols-outlined text-on-surface-variant">expand_more</span>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/login" class="w-full py-3 bg-primary text-on-primary font-title-md rounded-xl hover:bg-primary-container transition-colors shadow-md shadow-primary/20 flex items-center justify-center gap-2 cursor-pointer">
                            <span class="material-symbols-outlined text-[20px]">meeting_room</span>
                            <span>Book This Room</span>
                        </a>
                        <div class="text-center text-body-md text-on-surface-variant">You won't be charged yet</div>
                        <div class="flex flex-col gap-2 pt-4 border-t border-outline-variant">
                            <div class="flex justify-between text-body-md">
                                <span class="underline text-on-surface-variant cursor-pointer">3 nights x $<fmt:formatNumber value="${room.basePrice}" pattern="#,##0.00"/></span>
                                <span class="text-on-background font-medium">$<fmt:formatNumber value="${room.basePrice * 3}" pattern="#,##0.00"/></span>
                            </div>
                            <div class="flex justify-between text-body-md">
                                <span class="underline text-on-surface-variant cursor-pointer">Taxes and fees (15%)</span>
                                <span class="text-on-background font-medium">$<fmt:formatNumber value="${room.basePrice * 3 * 0.15}" pattern="#,##0.00"/></span>
                            </div>
                            <div class="flex justify-between text-title-md font-title-md pt-2 mt-2 border-t border-outline-variant">
                                <span class="text-on-background font-bold">Total</span>
                                <span class="text-on-background font-bold text-primary">$<fmt:formatNumber value="${room.basePrice * 3 * 1.15}" pattern="#,##0.00"/></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- Reviews Section -->
    <div class="border-t border-outline-variant pt-xl mt-lg">
        <h2 class="text-headline-md font-headline-md text-on-background mb-lg flex items-center gap-2">
            <span class="material-symbols-outlined text-tertiary-container" data-weight="fill">star</span>
            4.9 out of 5 · 128 reviews
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-gutter">
            <!-- Review Card 1 -->
            <div class="p-lg bg-surface-container-lowest border border-outline-variant rounded-2xl flex flex-col gap-sm shadow-sm">
                <div class="flex items-center gap-3">
                    <div class="w-12 h-12 bg-primary-fixed text-primary font-title-lg font-bold flex items-center justify-center rounded-full">SJ</div>
                    <div>
                        <div class="font-title-md font-semibold text-on-background">Sarah Jenkins</div>
                        <div class="text-caption text-on-surface-variant">September 2026</div>
                    </div>
                </div>
                <div class="flex text-tertiary-container gap-1">
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                </div>
                <p class="text-body-md text-on-surface-variant leading-relaxed">
                    Absolutely stunning room! The view was incredible, especially at night. The bed was remarkably comfortable and the amenities provided were top-notch. Will definitely be returning for my next trip.
                </p>
            </div>
            <!-- Review Card 2 -->
            <div class="p-lg bg-surface-container-lowest border border-outline-variant rounded-2xl flex flex-col gap-sm shadow-sm">
                <div class="flex items-center gap-3">
                    <div class="w-12 h-12 bg-tertiary-fixed text-tertiary font-title-lg font-bold flex items-center justify-center rounded-full">MR</div>
                    <div>
                        <div class="font-title-md font-semibold text-on-background">Michael Rodriguez</div>
                        <div class="text-caption text-on-surface-variant">August 2026</div>
                    </div>
                </div>
                <div class="flex text-tertiary-container gap-1">
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm" data-weight="fill">star</span>
                    <span class="material-symbols-outlined text-sm">star_half</span>
                </div>
                <p class="text-body-md text-on-surface-variant leading-relaxed">
                    Great experience overall. The room was spacious and meticulously clean. The bathroom is a real highlight with that huge soaking tub. Everything felt premium and high quality.
                </p>
            </div>
        </div>
    </div>
</main>

<!-- ========================================== -->
<!-- FULL-SCREEN PHOTO GALLERY MODAL / LIGHTBOX -->
<!-- ========================================== -->
<div id="galleryModal" style="display: none;" class="fixed inset-0 z-[100] bg-black/95 text-white flex flex-col backdrop-blur-md">
    <!-- Top Bar -->
    <div class="flex justify-between items-center px-6 py-4 border-b border-white/10 z-10">
        <div class="flex items-center gap-4">
            <button onclick="closeGallery()" class="p-2 hover:bg-white/10 rounded-full transition-colors flex items-center justify-center text-white cursor-pointer" title="Close (Esc)">
                <span class="material-symbols-outlined text-2xl">close</span>
            </button>
            <div>
                <h3 class="font-bold text-lg text-white"><c:out value="${room.typeName}"/> - Gallery</h3>
                <span id="modalCounter" class="text-xs text-white/70">Photo 1 of ${room.images.size()}</span>
            </div>
        </div>
        
        <div class="flex items-center gap-3">
            <button id="toggleViewBtn" onclick="toggleGalleryView()" class="px-3 py-1.5 bg-white/10 hover:bg-white/20 rounded-lg text-sm font-medium transition-colors flex items-center gap-1.5 text-white cursor-pointer">
                <span class="material-symbols-outlined text-sm" id="toggleViewIcon">grid_view</span>
                <span id="toggleViewText">Show Grid</span>
            </button>
        </div>
    </div>

    <!-- Main Slideshow View -->
    <div id="slideshowView" class="flex-1 relative flex flex-col items-center justify-center p-4 overflow-hidden">
        <div class="relative max-w-5xl max-h-[70vh] w-full h-full flex items-center justify-center">
            <img id="mainGalleryImage" class="max-w-full max-h-[70vh] object-contain rounded-xl shadow-2xl transition-opacity duration-300 select-none" src="${not empty room.images ? room.images[0] : ''}" alt="Room Photo">
        </div>

        <button onclick="prevPhoto()" class="absolute left-4 top-1/2 -translate-y-1/2 w-12 h-12 rounded-full bg-white/15 hover:bg-white/30 text-white flex items-center justify-center backdrop-blur-sm transition-all hover:scale-105 cursor-pointer" title="Previous photo (←)">
            <span class="material-symbols-outlined text-3xl">chevron_left</span>
        </button>
        <button onclick="nextPhoto()" class="absolute right-4 top-1/2 -translate-y-1/2 w-12 h-12 rounded-full bg-white/15 hover:bg-white/30 text-white flex items-center justify-center backdrop-blur-sm transition-all hover:scale-105 cursor-pointer" title="Next photo (→)">
            <span class="material-symbols-outlined text-3xl">chevron_right</span>
        </button>

        <div class="w-full max-w-4xl mt-4 px-4">
            <div id="thumbnailStrip" class="flex gap-2.5 overflow-x-auto py-2 no-scrollbar justify-center">
                <c:forEach items="${room.images}" var="img" varStatus="loop">
                    <button onclick="goToPhoto(${loop.index})" class="w-16 h-12 md:w-20 md:h-14 rounded-lg overflow-hidden flex-shrink-0 border-2 transition-all cursor-pointer ${loop.first ? 'border-primary-fixed ring-2 ring-primary-fixed/50 scale-105' : 'border-white/20 opacity-60 hover:opacity-100'}">
                        <img src="${img}" class="w-full h-full object-cover" alt="Thumb ${loop.index + 1}">
                    </button>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- Grid View Mode -->
    <div id="gridView" class="hidden flex-1 overflow-y-auto p-6 md:p-12 max-w-6xl w-full mx-auto">
        <div id="gridGalleryContainer" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
            <c:forEach items="${room.images}" var="img" varStatus="loop">
                <div onclick="selectPhotoFromGrid(${loop.index})" class="relative aspect-video rounded-xl overflow-hidden cursor-pointer group shadow-md border border-white/10 hover:border-primary-fixed transition-all">
                    <img src="${img}" alt="Photo ${loop.index + 1}" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300">
                    <div class="absolute bottom-2 left-2 bg-black/60 px-2 py-0.5 rounded text-xs text-white">#${loop.index + 1}</div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="w-full py-md px-6 md:px-12 flex flex-col md:flex-row justify-between items-center gap-sm bg-surface-container-low border-t border-outline-variant mt-auto">
    <span class="text-body-md font-body-md text-secondary">© 2026 LuxeStay Operational Systems. All Rights Reserved.</span>
    <div class="flex gap-lg text-caption">
        <a class="text-body-md font-body-md text-on-surface-variant hover:text-primary transition-colors opacity-80 hover:opacity-100" href="#">Privacy Policy</a>
        <a class="text-body-md font-body-md text-on-surface-variant hover:text-primary transition-colors opacity-80 hover:opacity-100" href="#">Support Docs</a>
        <a class="text-body-md font-body-md text-on-surface-variant hover:text-primary transition-colors opacity-80 hover:opacity-100" href="#">Contact Admin</a>
        <a class="text-body-md font-body-md text-on-surface-variant hover:text-primary transition-colors opacity-80 hover:opacity-100" href="#">System Status</a>
    </div>
</footer>

<script>
    const images = [
        <c:forEach items="${room.images}" var="img" varStatus="loop">
            "${img}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];
    let currentPhotoIndex = 0;
    let isGridView = false;

    function openGallery(initialIndex = 0) {
        if (!images || images.length === 0) return;
        currentPhotoIndex = (initialIndex >= 0 && initialIndex < images.length) ? initialIndex : 0;
        isGridView = false;

        const modal = document.getElementById('galleryModal');
        modal.style.display = 'flex';
        document.body.classList.add('overflow-hidden');

        renderGalleryView();
    }

    function closeGallery() {
        const modal = document.getElementById('galleryModal');
        modal.style.display = 'none';
        document.body.classList.remove('overflow-hidden');
    }

    function renderGalleryView() {
        const total = images.length;
        document.getElementById('modalCounter').textContent = `Photo ${currentPhotoIndex + 1} of ${total}`;

        if (isGridView) {
            document.getElementById('slideshowView').classList.add('hidden');
            document.getElementById('gridView').classList.remove('hidden');
            document.getElementById('toggleViewIcon').textContent = 'slideshow';
            document.getElementById('toggleViewText').textContent = 'Slideshow';
        } else {
            document.getElementById('slideshowView').classList.remove('hidden');
            document.getElementById('gridView').classList.add('hidden');
            document.getElementById('toggleViewIcon').textContent = 'grid_view';
            document.getElementById('toggleViewText').textContent = 'Show Grid';

            document.getElementById('mainGalleryImage').src = images[currentPhotoIndex];

            // Highlight thumbnail
            const thumbs = document.querySelectorAll('#thumbnailStrip button');
            thumbs.forEach((btn, idx) => {
                if (idx === currentPhotoIndex) {
                    btn.className = "w-16 h-12 md:w-20 md:h-14 rounded-lg overflow-hidden flex-shrink-0 border-2 transition-all cursor-pointer border-primary-fixed ring-2 ring-primary-fixed/50 scale-105";
                } else {
                    btn.className = "w-16 h-12 md:w-20 md:h-14 rounded-lg overflow-hidden flex-shrink-0 border-2 transition-all cursor-pointer border-white/20 opacity-60 hover:opacity-100";
                }
            });
        }
    }

    function toggleGalleryView() {
        isGridView = !isGridView;
        renderGalleryView();
    }

    function selectPhotoFromGrid(index) {
        currentPhotoIndex = index;
        isGridView = false;
        renderGalleryView();
    }

    function goToPhoto(index) {
        if (!images || images.length === 0) return;
        currentPhotoIndex = index;
        renderGalleryView();
    }

    function nextPhoto() {
        if (!images || images.length === 0) return;
        currentPhotoIndex = (currentPhotoIndex + 1) % images.length;
        renderGalleryView();
    }

    function prevPhoto() {
        if (!images || images.length === 0) return;
        currentPhotoIndex = (currentPhotoIndex - 1 + images.length) % images.length;
        renderGalleryView();
    }

    window.addEventListener('keydown', (e) => {
        const modal = document.getElementById('galleryModal');
        if (modal && modal.style.display !== 'none') {
            if (e.key === 'Escape') closeGallery();
            if (e.key === 'ArrowRight') nextPhoto();
            if (e.key === 'ArrowLeft') prevPhoto();
        }
    });
</script>

</body>
</html>
