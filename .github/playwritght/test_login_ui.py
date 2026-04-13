import re

from playwright.sync_api import Page, expect

BASE_URL = "http://localhost:8080/vista/login.xhtml"
LOGIN_OK_URL = re.compile(r".*/vista/inicio_emp(?:\.xhtml)?(?:\?.*)?$")

def test_login_exitoso(page):
    page.goto(BASE_URL)
    page.locator("input[type='text']").first.fill("julio.dental@dentalpatron.com")
    page.locator("input[type='password']").first.fill("julio123")
    page.get_by_role("button", name="Iniciar sesi\u00f3n").click()
    expect(page).to_have_url(LOGIN_OK_URL, timeout=30000)

def test_login_incorrecto(page):
    page.goto(BASE_URL)
    page.locator("input[type='text']").first.fill("julio.dental@dentalpatron.com")
    page.locator("input[type='password']").first.fill("incorrecta")
    page.get_by_role("button", name="Iniciar sesi\u00f3n").click()
    expect(page.locator(".ui-growl-message").first).to_be_visible()

def test_login_vacio(page):
    page.goto(BASE_URL)
    page.get_by_role("button", name="Iniciar sesi\u00f3n").click()
    expect(page.locator(".ui-growl-message").first).to_be_visible()

def test_titulo_pagina(page):
    page.goto(BASE_URL)
    assert page.title() == "Iniciar Sesión - Dental Patron"

