function formatarData(data) {
  if (!data || !/^\d{4}-\d{2}-\d{2}$/.test(data)) return "—";
  const [ano, mes, dia] = data.split("-");
  return dia + "/" + mes + "/" + ano;
}

function somenteNumeros(valor) {
  return String(valor || "").replace(/\D/g, "");
}

function formatarCPF(valor) {
  const numeros = somenteNumeros(valor).slice(0, 11);
  return numeros.replace(/(\d{3})(\d)/, "$1.$2").replace(/(\d{3})(\d)/, "$1.$2").replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

function formatarTelefone(valor) {
  const n = somenteNumeros(valor).slice(0, 11);
  if (n.length <= 2) return n.length ? "(" + n : "";
  if (n.length <= 10) return n.replace(/^(\d{2})(\d{0,4})(\d{0,4}).*/, (_, d, a, b) => "(" + d + ")" + a + (b ? "-" + b : ""));
  return n.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, "($1)$2-$3");
}

function exibirErro(campo, mensagem) {
  const elemento = document.querySelector('[data-error-for="' + campo + '"]');
  const wrapper = elemento?.closest(".field");
  if (elemento) elemento.textContent = mensagem;
  wrapper?.classList.toggle("invalid", Boolean(mensagem));
}

function limparErros() {
  document.querySelectorAll(".field-error").forEach(el => { el.textContent = ""; });
  document.querySelectorAll(".field.invalid").forEach(el => el.classList.remove("invalid"));
}

function configurarLogin() {
  const form = document.getElementById("login-form");
  if (!form) return;
  form.addEventListener("submit", async evento => {
    evento.preventDefault();
    const email = document.getElementById("login-email").value.trim();
    const senha = document.getElementById("login-password").value;
    const feedback = document.getElementById("login-feedback");
    const botao = form.querySelector('button[type="submit"]');
    if (!email || !senha) {
      feedback.textContent = "Preencha o e-mail e a senha para continuar.";
      feedback.className = "form-feedback error";
      return;
    }
    botao.disabled = true;
    feedback.textContent = "Validando acesso...";
    feedback.className = "form-feedback";
    try {
      const csrfResponse = await fetch("/api/auth/csrf", { credentials: "same-origin" });
      if (!csrfResponse.ok) throw new Error("Não foi possível validar o token de segurança.");
      const csrf = await csrfResponse.json();
      const resposta = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json", "X-CSRF-TOKEN": csrf.token },
        credentials: "same-origin",
        body: JSON.stringify({ email, senha })
      });
      if (!resposta.ok) {
        feedback.textContent = resposta.status === 401
          ? "E-mail ou senha inválidos."
          : "Não foi possível realizar o acesso. Tente novamente.";
        feedback.className = "form-feedback error";
        return;
      }
      window.location.href = "dashboard.html";
    } catch (erro) {
      feedback.textContent = "Não foi possível conectar ao servidor.";
      feedback.className = "form-feedback error";
    } finally {
      botao.disabled = false;
    }
  });
}

async function verificarSessao() {
  try {
    const resposta = await fetch("/api/auth/me", { credentials: "same-origin" });
    if (resposta.ok) return true;
  } catch (erro) {
    console.warn("Não foi possível validar a sessão.", erro);
  }
  window.location.replace("/");
  return false;
}

function configurarCadastro() {
  const form = document.getElementById("patient-form");
  if (!form) return;
  const cpfInput = document.getElementById("patient-cpf");
  const telefoneInput = document.getElementById("patient-phone");
  cpfInput.addEventListener("input", () => { cpfInput.value = formatarCPF(cpfInput.value); });
  telefoneInput.addEventListener("input", () => { telefoneInput.value = formatarTelefone(telefoneInput.value); });
  form.addEventListener("reset", () => {
    setTimeout(() => { limparErros(); const feedback = document.getElementById("patient-feedback"); feedback.textContent = ""; feedback.className = "form-feedback"; }, 0);
  });
  form.addEventListener("submit", async evento => {
    evento.preventDefault();
    limparErros();
    const dados = Object.fromEntries(new FormData(form).entries());
    let valido = true;
    const falhar = (campo, mensagem) => { exibirErro(campo, mensagem); valido = false; };
    if (!dados.nome.trim()) falhar("nome", "Informe o nome completo.");
    if (dados.nome.trim().length > 55) falhar("nome", "Use no máximo 55 caracteres.");
    if (somenteNumeros(dados.cpf).length !== 11) falhar("cpf", "Informe 11 dígitos para o CPF.");
    if (!dados.nascimento) falhar("nascimento", "Informe a data de nascimento.");
    else {
      const data = new Date(dados.nascimento + "T00:00:00");
      if (Number.isNaN(data.getTime()) || data > new Date()) falhar("nascimento", "Informe uma data válida, que não seja futura.");
    }
    const telefone = somenteNumeros(dados.telefone);
    if (telefone.length < 10 || telefone.length > 11) falhar("telefone", "Informe um telefone com DDD.");
    if (dados.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(dados.email)) falhar("email", "Informe um e-mail válido.");
    if (!dados.endereco.trim()) falhar("endereco", "Informe o endereço.");
    if (dados.endereco.trim().length > 200) falhar("endereco", "Use no máximo 200 caracteres.");
    if (!dados.convenio) falhar("convenio", "Selecione um convênio.");
    const feedback = document.getElementById("patient-feedback");
    const botao = form.querySelector('button[type="submit"]');
    if (!valido) {
      feedback.textContent = "Revise os campos destacados antes de salvar.";
      feedback.className = "form-feedback error";
      return;
    }
    botao.disabled = true;
    feedback.textContent = "Salvando paciente no banco de dados...";
    feedback.className = "form-feedback";
    try {
      const csrfResponse = await fetch("/api/auth/csrf", { credentials: "same-origin" });
      if (!csrfResponse.ok) throw new Error("Não foi possível validar o token de segurança.");
      const csrf = await csrfResponse.json();
      const resposta = await fetch("/api/pacientes", {
        method: "POST",
        headers: { "Content-Type": "application/json", "X-CSRF-TOKEN": csrf.token },
        credentials: "same-origin",
        body: JSON.stringify({
          nome: dados.nome.trim(),
          cpf: dados.cpf,
          nascimento: dados.nascimento,
          telefone: dados.telefone,
          email: dados.email.trim() || null,
          endereco: dados.endereco.trim(),
          convenio: dados.convenio
        })
      });
      if (resposta.status === 401) {
        window.location.replace("/");
        return;
      }
      if (resposta.status === 409) {
        exibirErro("cpf", "Este CPF já está cadastrado.");
        feedback.textContent = "Não foi possível cadastrar: CPF duplicado.";
        feedback.className = "form-feedback error";
        return;
      }
      if (!resposta.ok) {
        feedback.textContent = resposta.status === 400
          ? "Confira os dados informados. O servidor recusou o cadastro."
          : "Não foi possível salvar o paciente. Tente novamente.";
        feedback.className = "form-feedback error";
        return;
      }
      const criado = await resposta.json();
      form.reset();
      setTimeout(() => {
        feedback.textContent = "Paciente #" + criado.id + " cadastrado no banco de dados com sucesso.";
        feedback.className = "form-feedback";
      }, 0);
    } catch (erro) {
      feedback.textContent = "Falha de comunicação com o servidor. Nenhuma confirmação de cadastro foi recebida.";
      feedback.className = "form-feedback error";
    } finally {
      botao.disabled = false;
    }
  });
}

function escaparHTML(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, caractere => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[caractere]);
}

function configurarConsulta() {
  const tabela = document.getElementById("patients-table");
  if (!tabela) return;
  const campo = document.getElementById("patient-search");
  const contador = document.getElementById("result-count");
  const feedback = document.getElementById("search-feedback");
  let requisicaoAtual = 0;

  const renderizar = async () => {
    const numeroRequisicao = ++requisicaoAtual;
    const termo = campo.value.trim();
    contador.textContent = "Consultando banco de dados...";
    feedback.textContent = "";
    try {
      const resposta = await fetch("/api/pacientes?busca=" + encodeURIComponent(termo), {
        credentials: "same-origin"
      });
      if (resposta.status === 401) {
        window.location.replace("/");
        return;
      }
      if (!resposta.ok) throw new Error("Falha ao consultar pacientes.");
      const registros = await resposta.json();
      if (numeroRequisicao !== requisicaoAtual) return;
      contador.textContent = registros.length + (registros.length === 1 ? " registro encontrado" : " registros encontrados");
      tabela.innerHTML = registros.length ? registros.map(p => '<tr><td class="table-id">#' + escaparHTML(p.id) + '</td><td class="table-name">' + escaparHTML(p.nome) + '</td><td>' + escaparHTML(p.cpf ? formatarCPF(p.cpf) : "—") + '</td><td>' + escaparHTML(formatarData(p.nascimento)) + '</td><td>' + escaparHTML(p.idade ?? "—") + '</td><td>' + escaparHTML(p.telefone || "—") + '</td><td><span class="status-pill">' + escaparHTML(p.convenio || "—") + '</span></td></tr>').join("") : '<tr><td colspan="7" class="empty-state">Nenhum paciente corresponde à pesquisa.</td></tr>';
    } catch (erro) {
      contador.textContent = "Consulta indisponível";
      tabela.innerHTML = '<tr><td colspan="7" class="empty-state">Não foi possível carregar os pacientes do servidor.</td></tr>';
      feedback.textContent = "Verifique a conexão e tente novamente.";
      feedback.className = "form-feedback error";
    }
  };

  campo.addEventListener("input", renderizar);
  document.getElementById("clear-search").addEventListener("click", () => {
    campo.value = "";
    renderizar();
    campo.focus();
  });
  renderizar();
}

async function configurarPainel() {
  const contador = document.getElementById("dashboard-count");
  if (!contador) return;
  try {
    const resposta = await fetch("/api/pacientes", { credentials: "same-origin" });
    if (resposta.status === 401) {
      window.location.replace("/");
      return;
    }
    if (!resposta.ok) throw new Error("Falha ao consultar pacientes.");
    const pacientes = await resposta.json();
    contador.textContent = pacientes.length;
  } catch (erro) {
    contador.textContent = "—";
    console.warn("Não foi possível carregar o total de pacientes.", erro);
  }
}

function configurarLogout() {
  const link = document.getElementById("logout-link");
  if (!link) return;
  link.addEventListener("click", async evento => {
    evento.preventDefault();
    try {
      const csrfResponse = await fetch("/api/auth/csrf", { credentials: "same-origin" });
      if (csrfResponse.ok) {
        const csrf = await csrfResponse.json();
        await fetch("/api/auth/logout", { method: "POST", headers: { "X-CSRF-TOKEN": csrf.token }, credentials: "same-origin" });
      }
    } finally {
      window.location.replace("/");
    }
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  configurarLogin();
  if (document.getElementById("login-form")) return;
  if (!await verificarSessao()) return;
  configurarCadastro();
  configurarConsulta();
  configurarPainel();
  configurarLogout();
});
