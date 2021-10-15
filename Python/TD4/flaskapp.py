#!/usr/bin/python

from flask import Flask, url_for
import random

app = Flask(__name__)

jurons = [
	"Inomable",
	"Mille milliards de mille sabords",
	"Bougre d'ectoplasme de moule à gaufres",
	"Espèce de cornichon",
	"Graine de vaurien",
	"Mille tonnerres",
	"Esclavagiste",
	"Malotru",
	"Garnements",
	"Ectoplasme",
	"Catachrèses",
	"Clowns",
	"Ornithorynque",
	"Judas",
	"Rocambole",
	"Pays de tonnerre de Brest",
	"Iconoclastes",
	"Misérable iconoclaste",
	"Sales moustiques",
	"Sacripant"
]

@app.route("/")
def home():
	return "<h1>Bienvenue</h1><ul><li>/jure (une injure)</li><li>/jure/{n} ({n} injures)</li><li>/portrait (un portrait)</li></ul>"

@app.route("/jure")
def jure():
	juron = random.choices(jurons)[0]
	return f"<h1>{juron}</h1>"

@app.route("/jure/<int:n>")
def jure_n(n):
	chosen = random.sample(jurons, n)
	response = f"<h1>Les {n} jurons choisis</h1><ul>"
	for juron in chosen:
		response += f"<li>{juron}</li>"
	response += "</ul>"
	return response

@app.route("/portrait")
def portrait():
	img_src = url_for('static', filename='haddock.jpg')
	return f"<img src={img_src}/>"
	
		
