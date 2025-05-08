// Variables globales
const API_URL = "/api/movies"
let currentMovieId = null

// Éléments DOM
const movieContainer = document.getElementById("movies-container")
const movieForm = document.getElementById("movie-form")
const formTitle = document.getElementById("form-title")
const searchInput = document.getElementById("search-input")
const searchType = document.getElementById("search-type")
const searchBtn = document.getElementById("search-btn")
const deleteModal = document.getElementById("delete-modal")
const confirmDeleteBtn = document.getElementById("confirm-delete")
const cancelDeleteBtn = document.getElementById("cancel-delete")
const alertModal = document.getElementById("alert-modal")
const alertTitle = document.getElementById("alert-title")
const alertMessage = document.getElementById("alert-message")
const alertOkBtn = document.getElementById("alert-ok")

// Navigation
const navHome = document.getElementById("nav-home")
const navAdd = document.getElementById("nav-add")
const navAbout = document.getElementById("nav-about")
const moviesSection = document.getElementById("movies-section")
const searchSection = document.getElementById("search-section")
const addMovieSection = document.getElementById("add-movie-section")
const aboutSection = document.getElementById("about-section")

// Événements de navigation
navHome.addEventListener("click", (e) => {
    e.preventDefault()
    showSection(moviesSection)
    showSection(searchSection)
    hideSection(addMovieSection)
    hideSection(aboutSection)
    setActiveNav(navHome)
    loadMovies()
})

navAdd.addEventListener("click", (e) => {
    e.preventDefault()
    hideSection(moviesSection)
    hideSection(searchSection)
    showSection(addMovieSection)
    hideSection(aboutSection)
    setActiveNav(navAdd)
    resetForm()
})

navAbout.addEventListener("click", (e) => {
    e.preventDefault()
    hideSection(moviesSection)
    hideSection(searchSection)
    hideSection(addMovieSection)
    showSection(aboutSection)
    setActiveNav(navAbout)
})

// Fonction pour définir le lien actif dans la navigation
function setActiveNav(activeLink) {
    navHome.classList.remove("active")
    navAdd.classList.remove("active")
    navAbout.classList.remove("active")
    activeLink.classList.add("active")
}

// Fonctions pour afficher/masquer les sections
function showSection(section) {
    section.classList.remove("hidden")
}

function hideSection(section) {
    section.classList.add("hidden")
}

// Chargement des films
async function loadMovies() {
    try {
        movieContainer.innerHTML = '<div class="loading">Chargement des films...</div>'
        const response = await fetch(API_URL)

        if (!response.ok) {
            throw new Error("Erreur lors du chargement des films")
        }

        const movies = await response.json()
        displayMovies(movies)
    } catch (error) {
        showAlert("Erreur", error.message)
        movieContainer.innerHTML = '<div class="loading">Erreur lors du chargement des films</div>'
    }
}

// Affichage des films
function displayMovies(movies) {
    if (movies.length === 0) {
        movieContainer.innerHTML = '<div class="loading">Aucun film trouvé</div>'
        return
    }

    movieContainer.innerHTML = ""

    movies.forEach((movie) => {
        const movieCard = document.createElement("div")
        movieCard.className = "movie-card"

        const releaseDate = new Date(movie.releaseDate).toLocaleDateString()

        movieCard.innerHTML = `
      <div class="movie-header">
        <h3 class="movie-title">${movie.title}</h3>
        <div class="movie-rating">${movie.rating}</div>
      </div>
      <div class="movie-body">
        <div class="movie-info"><span>Réalisateur:</span> ${movie.director}</div>
        <div class="movie-info"><span>Date de sortie:</span> ${releaseDate}</div>
        <div class="movie-info"><span>Genre:</span> ${movie.genre}</div>
        <div class="movie-actions">
          <button class="btn btn-edit" data-id="${movie.id}"><i class="fas fa-edit"></i> Modifier</button>
          <button class="btn btn-delete" data-id="${movie.id}"><i class="fas fa-trash"></i> Supprimer</button>
        </div>
      </div>
    `

        movieContainer.appendChild(movieCard)

        // Ajouter les événements aux boutons
        const editBtn = movieCard.querySelector(".btn-edit")
        const deleteBtn = movieCard.querySelector(".btn-delete")

        editBtn.addEventListener("click", () => editMovie(movie.id))
        deleteBtn.addEventListener("click", () => openDeleteModal(movie.id))
    })
}

// Recherche de films
searchBtn.addEventListener("click", async () => {
    const searchValue = searchInput.value.trim()
    const type = searchType.value

    if (!searchValue) {
        loadMovies()
        return
    }

    try {
        let url = API_URL

        if (type === "genre") {
            url = `${API_URL}/genre/${searchValue}`
        } else if (type === "director") {
            url = `${API_URL}/director/${searchValue}`
        } else {
            // Pour le titre, on charge tous les films et on filtre côté client
            const response = await fetch(API_URL)
            if (!response.ok) {
                throw new Error("Erreur lors de la recherche")
            }

            const movies = await response.json()
            const filteredMovies = movies.filter((movie) => movie.title.toLowerCase().includes(searchValue.toLowerCase()))

            displayMovies(filteredMovies)
            return
        }

        const response = await fetch(url)
        if (!response.ok) {
            throw new Error("Erreur lors de la recherche")
        }

        const movies = await response.json()
        displayMovies(movies)
    } catch (error) {
        showAlert("Erreur", error.message)
    }
})

// Gestion du formulaire
movieForm.addEventListener("submit", async (e) => {
    e.preventDefault()

    const movieData = {
        title: document.getElementById("title").value,
        director: document.getElementById("director").value,
        releaseDate: document.getElementById("release-date").value,
        genre: document.getElementById("genre").value,
        rating: Number.parseFloat(document.getElementById("rating").value),
    }

    try {
        let url = API_URL
        let method = "POST"

        if (currentMovieId) {
            url = `${API_URL}/${currentMovieId}`
            method = "PUT"
        }

        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(movieData),
        })

        if (!response.ok) {
            throw new Error("Erreur lors de l'enregistrement du film")
        }

        showAlert("Succès", "Film enregistré avec succès")
        resetForm()
        navHome.click()
    } catch (error) {
        showAlert("Erreur", error.message)
    }
})

// Annulation du formulaire
document.getElementById("cancel-btn").addEventListener("click", () => {
    resetForm()
    navHome.click()
})

// Réinitialisation du formulaire
function resetForm() {
    movieForm.reset()
    currentMovieId = null
    formTitle.textContent = "Ajouter un nouveau film"
    document.getElementById("movie-id").value = ""
}

// Édition d'un film
async function editMovie(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`)

        if (!response.ok) {
            throw new Error("Erreur lors du chargement du film")
        }

        const movie = await response.json()

        // Remplir le formulaire
        document.getElementById("movie-id").value = movie.id
        document.getElementById("title").value = movie.title
        document.getElementById("director").value = movie.director
        document.getElementById("release-date").value = movie.releaseDate
        document.getElementById("genre").value = movie.genre
        document.getElementById("rating").value = movie.rating

        currentMovieId = movie.id
        formTitle.textContent = "Modifier le film"

        // Afficher la section du formulaire
        navAdd.click()
    } catch (error) {
        showAlert("Erreur", error.message)
    }
}

// Suppression d'un film
function openDeleteModal(id) {
    currentMovieId = id
    deleteModal.style.display = "flex"
}

confirmDeleteBtn.addEventListener("click", async () => {
    try {
        const response = await fetch(`${API_URL}/${currentMovieId}`, {
            method: "DELETE",
        })

        if (!response.ok) {
            throw new Error("Erreur lors de la suppression du film")
        }

        deleteModal.style.display = "none"
        showAlert("Succès", "Film supprimé avec succès")
        loadMovies()
    } catch (error) {
        deleteModal.style.display = "none"
        showAlert("Erreur", error.message)
    }
})

cancelDeleteBtn.addEventListener("click", () => {
    deleteModal.style.display = "none"
    currentMovieId = null
})

// Affichage des alertes
function showAlert(title, message) {
    alertTitle.textContent = title
    alertMessage.textContent = message
    alertModal.style.display = "flex"
}

alertOkBtn.addEventListener("click", () => {
    alertModal.style.display = "none"
})

// Chargement initial
document.addEventListener("DOMContentLoaded", () => {
    navHome.click()
})
