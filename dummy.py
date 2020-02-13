import pandas as pd
import random
from faker import Faker
from faker.providers import BaseProvider

Faker.seed(4321)
fake = Faker()

class MyProvider(BaseProvider):
    def status(self):
        position_statuses = ['External', 'Leaver', 'Occupied', 'Vacant', 'Pending Approval']
        
        # We select a random destination from the list and return it
        return random.choice(position_statuses)
    
    def recruitment_status(self):
        recruitment_statuses = ['Approved', 'Contracted', 'Deactivated', 'Extended', 'External', 'On-Board', 'Proposed']
        
        # We select a random destination from the list and return it
        return random.choice(recruitment_statuses)
    
    def pillar(self):
        pillars = ['Online', 'Protech', 'Retail', 'Business Framework']
        
        # We select a random destination from the list and return it
        return random.choice(pillars)
    
    def company(self):
        companies = ['GM Malta', 'Sports Services South America', 'Sport Bet', 'GM Technology Services', 'GM Retail Services', 'GM Retail Systems', 'GM Casino', 'GM Group', 'GM Shop Agency North', 'GM Shop Agency East']
        
        # We select a random destination from the list and return it
        return random.choice(companies)
    
    def department(self):
        departments = ['Acquisition Marketing - Online', 'Admin', 'Bookmaking', 'Business Development', 'C-Level', 'Compliance & Regulations', 'Customer Services', 'Finance', 'Gaming - Online', 'Graduates - Trainees', 'HR', 'Legal', 'Marketing Intelligence', 'Payments & Fraud Services', 'PMO', 'Protech', 'Protech - Data', 'Retail', 'Retail Services & Legal Affairs', 'Retention Marketing - Online', 'GM Gibraltar', 'TRServ', 'TRSys', 'TSA EAST - Retail', 'TSA North - Retail']
        
        # We select a random destination from the list and return it
        return random.choice(departments)
    
    def function(self):
        functions = ['Acquisition Marketing', 'Bookmaking Colombia', 'Bookmaking Croatia', 'Bookmaking Malta', 'Business Development', 'Compliance & Regulations', 'Corp. Communications', 'Customer Services', 'Executives', 'Finance', 'Gaming', 'HR', 'Legal', 'Marketing Intelligence', 'Office Services', 'Payments & Fraud Services', 'PMO', 'Protech', 'Protech - Bookmaking', 'Protech - CTP', 'Protech - Data', 'Protech - Digital Tech', 'Protech - Infrastructure', 'Protech - Retail Tech', 'Protech - Tech Digital', 'Retail Services', 'Retail Services & Legal Affairs', 'Retail Systems', 'Retention Marketing', 'Sponsoring', 'GM Gibraltar', 'TSA East', 'TSA North']
        
        # We select a random destination from the list and return it
        return random.choice(functions)

# Add the provider to our faker object
fake.add_provider(MyProvider)

rows = 800
position_status = []
recruitment_status = []
number = []
pillar = []
company = []
department = []
function = []
title = []
functional_reporting_line = []
disciplinary_reporting_line = []
holder = []
hours = []
start_date = []
end_date = []
salary = []
social_security_contribution = []
fringe_benefit = []
performance_bonus = []
super_bonus = []
management_bonus = []

for _ in range(rows):
    position_status.append(fake.status())
    recruitment_status.append(fake.recruitment_status())
    number.append(fake.lexify(text="???", letters="ABCDEFGHIJKLMNOPQRSTUVWXYZ") + fake.numerify(text="###"))
    pillar.append(fake.pillar())
    company.append(fake.company())
    department.append(fake.department())
    function.append(fake.function())
    title.append(fake.job())
    functional_reporting_line.append(fake.lexify(text="???", letters="ABCDEFGHIJKLMNOPQRSTUVWXYZ") + fake.numerify(text="###"))
    disciplinary_reporting_line.append(fake.lexify(text="???", letters="ABCDEFGHIJKLMNOPQRSTUVWXYZ") + fake.numerify(text="###"))
    holder.append(fake.name())
    hours.append(fake.random_int(min=15, max=40))
    start_date.append(fake.date_between(start_date="-5y", end_date="today"))
    end_date.append(fake.future_date(end_date="+180d", tzinfo=None))
    salary.append(fake.random_int(min=20000, max=1000000))
    social_security_contribution.append(fake.random_int(min=3000, max=15000))
    fringe_benefit.append(fake.random_int(min=0, max=100))
    performance_bonus.append(fake.random_int(min=0, max=15))
    super_bonus.append(15)
    management_bonus.append(fake.random_int(min=20000, max=100000))

data = list(zip(position_status, recruitment_status, number, pillar, company, department, function, title, functional_reporting_line, disciplinary_reporting_line, holder, hours, start_date, end_date, salary, social_security_contribution, fringe_benefit, performance_bonus, super_bonus, management_bonus))
columns = ['Status', 'Recruitment Status', 'Number', 'Pillar', 'Company', 'Department', 'Function', 'Title', 'Functional Reporting Line', 'Disciplinary Reporting Line', 'Holder', 'Hours', 'Start Date', 'End Date', 'Salary', 'Social Security Contribution', 'Fringe Benefit', 'Performance Bonus', 'Super Bonus', 'Management Bonus']
position = pd.DataFrame(data=data, columns=columns)
position.to_csv('position.csv', index=False)

# re-initialise lists for budget
position_status = []
recruitment_status = []
holder = []
start_date = []
end_date = []
salary = []
social_security_contribution = []
fringe_benefit = []
performance_bonus = []
management_bonus = []

for _ in range(rows):
    position_status.append(fake.status())
    recruitment_status.append(fake.recruitment_status())
    holder.append(fake.name())
    start_date.append(fake.date_between(start_date="-5y", end_date="today"))
    end_date.append(fake.future_date(end_date="+180d", tzinfo=None))
    salary.append(fake.random_int(min=20000, max=1000000))
    social_security_contribution.append(fake.random_int(min=3000, max=15000))
    fringe_benefit.append(fake.random_int(min=0, max=100))
    performance_bonus.append(fake.random_int(min=0, max=15))
    management_bonus.append(fake.random_int(min=20000, max=100000))

data = list(zip(position_status, recruitment_status, number, pillar, company, department, function, title, functional_reporting_line, disciplinary_reporting_line, holder, hours, start_date, end_date, salary, social_security_contribution, fringe_benefit, performance_bonus, super_bonus, management_bonus))
columns = ['Status', 'Recruitment Status', 'Number', 'Pillar', 'Company', 'Department', 'Function', 'Title', 'Functional Reporting Line', 'Disciplinary Reporting Line', 'Holder', 'Hours', 'Start Date', 'End Date', 'Salary', 'Social Security Contribution', 'Fringe Benefit', 'Performance Bonus', 'Super Bonus', 'Management Bonus']
budget = pd.DataFrame(data=data, columns=columns)
budget.to_csv('budget.csv', index=False)