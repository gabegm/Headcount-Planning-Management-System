import io

from setuptools import find_packages, setup

with io.open('README.md', 'rt', encoding='utf8') as f:
    readme = f.read()

setup(
    name='gatekeeping',
    version='0.1',
    url='https://bitbucket.booxdev.com/users/gaucimaistreg/repos/gatekeeping/',
    license="MIT",
    maintainer='Gabriel Gauci Maistre',
    maintainer_email='gabriel@gaucimaistre.com',
    description='',
    long_description=readme,
    packages=find_packages(),
    include_package_data=True,
    zip_safe=False,
    install_requires=[
        'flask', 'pandas',
    ],
    extras_require={
        'test': [
            'pytest',
            'coverage',
        ],
    },
)
